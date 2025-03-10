package com.project2.domain.member.service;

import com.project2.domain.member.entity.Member;
import com.project2.domain.member.repository.MemberRepository;
import com.project2.global.exception.ServiceException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private AuthTokenService authTokenService;

    @InjectMocks
    private AuthService authService;

    private Member mockMember;
    private final String validAccessToken = "validAccessToken";
    private final String invalidAccessToken = "invalidAccessToken";
    private final String validRefreshToken = "validRefreshToken";
    private final String invalidRefreshToken = "invalidRefreshToken";

    @BeforeEach
    void setUp() throws Exception {
        injectValue(authTokenService, "keyString", "test-secret-key"); // test용 keyString
        injectValue(authTokenService, "accessTokenExpireSeconds", 3600); // test용 accessToken
        injectValue(authTokenService, "refreshTokenExpireSeconds", 7200); // test용 refreshToken

        mockMember = Member.builder()
                .id(1L)
                .email("test@example.com")
                .build();
    }

    private void injectValue(Object target, String fieldName, Object value) throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }

    @Test
    @DisplayName("정상적인 Access Token을 사용하여 회원 정보를 조회할 수 있다.")
    void getMemberByAccessToken_shouldReturnMember_whenAccessTokenIsValid() {
        // Given
        Map<String, Object> payload = Map.of(
                "id", mockMember.getId(),
                "email", mockMember.getEmail()
        );
        when(authTokenService.getPayload(validAccessToken)).thenReturn(payload);

        // When
        Optional<Member> result = authService.getMemberByAccessToken(validAccessToken);

        // Then
        assertTrue(result.isPresent());
        assertEquals(mockMember.getId(), result.get().getId());
        assertEquals(mockMember.getEmail(), result.get().getEmail());
    }

    @Test
    @DisplayName("잘못된 Access Token을 사용하면 Optional.empty()를 반환한다.")
    void getMemberByAccessToken_shouldReturnEmpty_whenAccessTokenIsInvalid() {
        // Given
        when(authTokenService.getPayload(invalidAccessToken)).thenReturn(null);

        // When
        Optional<Member> result = authService.getMemberByAccessToken(invalidAccessToken);

        // Then
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("정상적인 Refresh Token을 사용하여 회원 정보를 조회할 수 있다.")
    void getMemberByRefreshToken_shouldReturnMember_whenRefreshTokenIsValid() {
        // Given
        Map<String, Object> payload = Map.of("id", mockMember.getId());
        when(authTokenService.getPayload(validRefreshToken)).thenReturn(payload);
        when(memberRepository.findById(mockMember.getId())).thenReturn(Optional.of(mockMember));

        // When
        Optional<Member> result = authService.getMemberByRefreshToken(validRefreshToken);

        // Then
        assertTrue(result.isPresent());
        assertEquals(mockMember.getId(), result.get().getId());
    }

    @Test
    @DisplayName("잘못된 Refresh Token을 사용하면 Optional.empty()를 반환한다.")
    void getMemberByRefreshToken_shouldReturnEmpty_whenRefreshTokenIsInvalid() {
        // Given
        when(authTokenService.getPayload(invalidRefreshToken)).thenReturn(null);

        // When
        Optional<Member> result = authService.getMemberByRefreshToken(invalidRefreshToken);

        // Then
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("존재하지 않는 회원 ID가 포함된 Refresh Token을 사용하면 Optional.empty()를 반환한다.")
    void getMemberByRefreshToken_shouldReturnEmpty_whenMemberDoesNotExist() {
        // Given
        Map<String, Object> payload = Map.of("id", 999L);
        when(authTokenService.getPayload(validRefreshToken)).thenReturn(payload);
        when(memberRepository.findById(999L)).thenReturn(Optional.empty());

        // When
        Optional<Member> result = authService.getMemberByRefreshToken(validRefreshToken);

        // Then
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("존재하지 않는 회원 ID로 getMemberByRefreshTokenOrThrow 호출 시 예외가 발생해야 한다.")
    void getMemberByRefreshTokenOrThrow_shouldThrowException_whenMemberDoesNotExist() {
        // Given
        Map<String, Object> payload = Map.of("id", 999L);
        when(authTokenService.getPayload(validRefreshToken)).thenReturn(payload);
        when(memberRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        ServiceException exception = assertThrows(ServiceException.class, () ->
                authService.getMemberByRefreshTokenOrThrow(validRefreshToken)
        );

        assertEquals("401", exception.getCode());
        assertEquals("유효하지 않은 리프레시 토큰이거나 회원을 찾을 수 없습니다.", exception.getMessage());
    }

    @Test
    @DisplayName("정상적인 Access Token이 생성되어야 한다.")
    void genAccessToken_shouldReturnToken_whenCalled() {
        // Given
        when(authTokenService.genAccessToken(mockMember)).thenReturn("newAccessToken");

        // When
        String token = authService.genAccessToken(mockMember);

        // Then
        assertEquals("newAccessToken", token);
    }

    @Test
    @DisplayName("정상적인 Refresh Token이 생성되어야 한다.")
    void genRefreshToken_shouldReturnToken_whenCalled() {
        // Given
        when(authTokenService.genRefreshToken(mockMember.getId())).thenReturn("newRefreshToken");

        // When
        String token = authService.genRefreshToken(mockMember);

        // Then
        assertEquals("newRefreshToken", token);
    }
}