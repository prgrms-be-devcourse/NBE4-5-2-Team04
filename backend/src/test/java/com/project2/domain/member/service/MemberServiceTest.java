package com.project2.domain.member.service;

import com.project2.domain.member.entity.Member;
import com.project2.domain.member.enums.Provider;
import com.project2.domain.member.repository.MemberRepository;
import com.project2.global.dto.OAuthUserInfo;
import com.project2.global.security.OAuth.OAuthService;
import com.project2.global.util.ImageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private OAuthService oAuthService;

    @Mock
    private ImageService imageService;

    @InjectMocks
    private MemberService memberService;

    private final String email = "test@example.com";
    private final String nickname = "TestUser";
    private final String authCode = "auth-code";
    private final String profileImageUrl = "/profile.jpg";
    private final Provider provider = Provider.GOOGLE;
    private Member mockMember;

    @BeforeEach
    void setUp() {
        mockMember = Member.builder()
                .email(email)
                .nickname(nickname)
                .provider(provider)
                .profileImageUrl(profileImageUrl)
                .build();
    }

    @Test
    @DisplayName("기존 회원이 존재하면 회원가입 없이 로그인만 수행된다.")
    void loginOrSignUp_MemberExists_ReturnsExistingMember() {
        // Given (기존 회원이 존재하는 경우)
        when(memberRepository.findByEmail(email)).thenReturn(Optional.of(mockMember));

        // When
        Member result = memberService.loginOrSignUp(email, nickname, authCode, provider);

        // Then
        assertNotNull(result);
        assertEquals(email, result.getEmail());
        verify(memberRepository, times(1)).findByEmail(email);
        verify(memberRepository, never()).save(any(Member.class)); // 새로운 회원 저장이 없어야 함
    }

    @Test
    @DisplayName("회원이 존재하지 않으면 OAuth 정보를 이용해 새로운 회원을 생성한다.")
    void loginOrSignUp_MemberNotExists_CreatesNewMember() {
        // Given (회원이 존재하지 않는 경우)
        when(memberRepository.findByEmail(email)).thenReturn(Optional.empty());
        OAuthUserInfo userInfo = new OAuthUserInfo(profileImageUrl);
        when(oAuthService.getOAuthUserInfo(provider, authCode)).thenReturn(userInfo);
        when(memberRepository.save(any(Member.class))).thenReturn(mockMember);

        // When
        Member result = memberService.loginOrSignUp(email, nickname, authCode, provider);

        // Then
        assertNotNull(result);
        assertEquals(email, result.getEmail());
        assertEquals(provider, result.getProvider());
        assertEquals(profileImageUrl, result.getProfileImageUrl());

        verify(memberRepository, times(1)).findByEmail(email);
        verify(oAuthService, times(1)).getOAuthUserInfo(provider, authCode);
        verify(imageService, times(1)).downloadProfileImage(provider, userInfo.getProfileImageUrl());
        verify(memberRepository, times(1)).save(any(Member.class));
    }
}
