package com.project2.domain.member.service;

import com.project2.domain.member.entity.Member;
import com.project2.domain.member.enums.Provider;
import com.project2.domain.member.repository.MemberRepository;
import com.project2.global.util.ImageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

    @Mock
    private MemberRepository memberRepository;
    @Mock
    private ImageService imageService;
    @InjectMocks
    private MemberService memberService;

    private final String email = "test@example.com";
    private final String nickname = "TestUser";
    private final String profileImageUrl = "/profile.jpg";
    private final Provider provider = Provider.GOOGLE;
    private Member mockMember;

    @BeforeEach
    void setUp() {
        mockMember = Member.builder()
                .id(1L)
                .email(email)
                .nickname(nickname)
                .provider(provider)
                .profileImageUrl(profileImageUrl)
                .build();
    }

    @Test
    @DisplayName("로그인을 수행된다.")
    void memberExists_ReturnsExistingMember() {
        // Given (기존 회원이 존재하는 경우)
        when(memberRepository.findByEmail(email)).thenReturn(Optional.of(mockMember));

        // When
        Optional<Member> result = memberService.findByEmail(email);

        // Then
        assertThat(result.isPresent()).isTrue();
        assertThat(email).isEqualTo(result.get().getEmail());
        assertThat(nickname).isEqualTo(result.get().getNickname());

        // `signUp`이 호출되지 않아야 함
        verify(memberRepository, never()).save(any(Member.class));
    }

    @Test
    @DisplayName("회원 가입을 수행한다.")
    void memberNotExists_CreatesNewMember() {
        // Given
        when(imageService.downloadProfileImage(anyString(), anyLong())).thenReturn("mocked/path/profile.png");
        when(memberRepository.save(any(Member.class))).thenReturn(mockMember);

        // When
        Member result = memberService.signUp(email, nickname, profileImageUrl, provider);

        // Then
        assertThat(result).isNotNull();
        assertThat(email).isEqualTo(result.getEmail());
        assertThat(provider).isEqualTo(result.getProvider());
        assertThat(result.getProfileImageUrl()).isEqualTo("mocked/path/profile.png");

        verify(memberRepository, times(1)).save(any(Member.class));
        verify(imageService, times(1)).downloadProfileImage(anyString(), anyLong());
    }
}
