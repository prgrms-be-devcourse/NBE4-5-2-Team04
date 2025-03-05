package com.project2.domain.member.service;

import com.project2.domain.member.entity.Member;
import com.project2.domain.member.enums.Provider;
import com.project2.global.util.Ut;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
@ExtendWith(MockitoExtension.class)
public class AuthTokenServiceTest {

    private final String keyString = "abcdefghijklmnopqrstuvwxyz123456"; // 테스트용 keyString(실제와 다를 수 있음)
    private final int accessTokenExpireSeconds = 3600; // 테스트용 accessTokenExpireSeconds(실제와 다를 수 있음)

    @InjectMocks
    private AuthTokenService authTokenService;  // Mock 주입

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        authTokenService = new AuthTokenService();
        ReflectionTestUtils.setField(authTokenService, "keyString", keyString);
        ReflectionTestUtils.setField(authTokenService, "accessTokenExpireSeconds", accessTokenExpireSeconds);
    }

    @Test
    @DisplayName("jwt 생성")
    void createToken() {
        Map<String, Object> originPayload = Map.of("name", "john", "age", 23);

        String jwtStr = Ut.Jwt.createToken(keyString, accessTokenExpireSeconds, originPayload);
        assertThat(jwtStr).isNotBlank();
        Map<String, Object> parsedPayload = Ut.Jwt.getPayload(keyString, jwtStr);

        assertThat(parsedPayload).containsAllEntriesOf(originPayload);
    }

    @Test
    @DisplayName("access token 생성")
    void accessToken() {
        Member mockMember = Member.builder()
            .id(1L)
            .email("test@test.com")
            .nickname("nickname")
            .provider(Provider.NAVER)
            .build();

        String accessToken = authTokenService.genAccessToken(mockMember);

        assertThat(accessToken).isNotBlank();
    }

    @Test
    @DisplayName("jwt valid check")
    void checkValid() {
        Member mockMember = Member.builder()
                .id(1L)
                .email("test@test.com")
                .nickname("nickname")
                .provider(Provider.NAVER)
                .build();

        String accessToken = authTokenService.genAccessToken(mockMember);

        boolean isValid = Ut.Jwt.isValidToken(keyString, accessToken);
        assertThat(isValid).isTrue();

        Map<String, Object> parsedPayload = authTokenService.getPayload(accessToken);

        assertThat(parsedPayload).containsEntry("id", mockMember.getId());
        assertThat(parsedPayload).containsEntry("email", mockMember.getEmail());
    }
}