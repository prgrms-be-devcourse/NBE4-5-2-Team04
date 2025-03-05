package com.project2.domain.member.dto;

import com.project2.domain.member.enums.Provider;
import jakarta.validation.constraints.Email;
import lombok.Getter;
import lombok.NonNull;

@Getter
public class OAuthAuthenticationRequestDto {

    @Email
    private String email;
    @NonNull
    private String nickname;
    private String authCode;
    @NonNull
    private Provider provider;

    public OAuthAuthenticationRequestDto(String email, String nickname, String authCode, Provider provider) {
        this.email = email;
        this.authCode = authCode;
        this.nickname = nickname;
        this.provider = provider;
    }
}