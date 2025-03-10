package com.project2.domain.member.service;

import com.project2.domain.member.entity.Member;
import com.project2.global.util.Ut;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthTokenService {

    @Value("${custom.jwt.secret-key}")
    private String keyString;

    @Value("${custom.jwt.access-token-expire-seconds}")
    private int accessTokenExpireSeconds;

    @Value("${custom.jwt.refresh-token-expire-seconds}")
    private int refreshTokenExpireSeconds;

    String genAccessToken(Member member) {

        return Ut.Jwt.createToken(
                keyString,
                accessTokenExpireSeconds,
                Map.of("id", member.getId(), "email", member.getEmail())
        );
    }

    public String genRefreshToken(Long id) {
        return Ut.Jwt.createToken(
                keyString,
                refreshTokenExpireSeconds,
                Map.of("id", id)
        );
    }

    Map<String, Object> getPayload(String token) {

        if(!Ut.Jwt.isValidToken(keyString, token)) return null;

        Map<String, Object> payload = Ut.Jwt.getPayload(keyString, token);
        Number idNo = (Number)payload.get("id");
        long id = idNo.longValue();
        String email = (payload.containsKey("email")) ? (String) payload.get("email") : "";

        return Map.of("id", id, "email", email);
    }
}