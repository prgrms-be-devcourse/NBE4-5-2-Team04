package com.project2.domain.member.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import com.project2.domain.member.entity.Member;
import com.project2.domain.member.enums.Provider;
import com.project2.domain.member.repository.MemberRepository;
import com.project2.global.security.SecurityUser;
import com.project2.global.util.ImageService;

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
	private SecurityUser actor;

	@BeforeEach
	void setUp() {
		mockMember = Member.builder()
			.id(1L)
			.email(email)
			.nickname(nickname)
			.provider(provider)
			.profileImageUrl(profileImageUrl)
			.build();

		actor = new SecurityUser(mockMember);
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

	@Test
	@DisplayName("닉네임을 수정한다")
	void memberExists_UpdateNickname() {
		// Given
		long memberId = 1L;
		String newNickname = "newNickname";

		// findByIdOrThrow()가 내부적으로 호출하는 findById()를 Mock 처리
		when(memberRepository.findById(memberId)).thenReturn(Optional.of(mockMember));

		// When
		Member updatedMember = memberService.updateNickname(memberId, newNickname);

		// Then
		assertThat(updatedMember.getNickname()).isEqualTo(newNickname);
		verify(memberRepository, times(1)).findById(memberId);
	}

	@Test
	@DisplayName("프로필 이미지를 수정한다")
	void memberExists_UpdateProfileImageUrl() {
		// Given
		long memberId = 1L;

		// Mock MultipartFile (가짜 파일 생성)
		MultipartFile mockFile = new MockMultipartFile(
			"profileImage",
			"profile.png",
			"image/png",
			"dummy image content".getBytes()
		);

		// Mock findByIdOrThrow()가 반환할 값 설정
		when(memberRepository.findById(memberId)).thenReturn(java.util.Optional.of(mockMember));

		// Mock imageService.storeProfileImage() 동작 설정 (가짜 파일 경로 반환)
		String mockFilePath = "/uploads/profiles/1/1710208701234.png";
		when(imageService.storeProfileImage(memberId, mockFile)).thenReturn(mockFilePath);

		// When
		Member updatedMember = memberService.updateProfileImageUrl(memberId, mockFile);

		// Then
		assertThat(updatedMember.getProfileImageUrl()).isEqualTo(mockFilePath);
		verify(memberRepository, times(1)).findById(memberId);
		verify(imageService, times(1)).storeProfileImage(memberId, mockFile);
	}
}
