package com.project2.domain.member.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.project2.domain.member.entity.Member;
import com.project2.domain.member.enums.Provider;
import com.project2.domain.member.service.AuthService;
import com.project2.global.security.Rq;
import com.project2.global.security.SecurityUser;

@ExtendWith(MockitoExtension.class)
class MemberControllerTest {

	@InjectMocks
	private MemberController memberController;

	@Mock
	private Rq rq;

	@Mock
	private AuthService authService;

	private MockMvc mockMvc;
	private Member mockMember;
	private SecurityUser actor;

	MemberControllerTest() {
	}

	@BeforeEach
	void setUp() {
		mockMvc = MockMvcBuilders.standaloneSetup(memberController).build();
		mockMember = Member.builder()
			.id(1L)
			.email("test@test.com")
			.nickname("test")
			.provider(Provider.NAVER)
			.createdDate(LocalDateTime.now())
			.build();

		actor = new SecurityUser(mockMember);

		// SecurityContext에 인증 정보 추가
		SecurityContext context = SecurityContextHolder.createEmptyContext();
		context.setAuthentication(new UsernamePasswordAuthenticationToken(actor, null, actor.getAuthorities()));
		SecurityContextHolder.setContext(context);
	}

	@Test
	@DisplayName("사용자 정보 조회 - 성공적으로 응답을 반환한다.")
	void getAuthenticatedMemberInfo_Success() throws Exception {
		// given
		when(rq.getActor()).thenReturn(mockMember);
		when(rq.getRealActor(mockMember)).thenReturn(mockMember);

		// when & then
		mockMvc.perform(get("/api/members/me"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.code").value("200"))
			.andExpect(jsonPath("$.data.id").value(mockMember.getId()))
			.andExpect(jsonPath("$.data.nickname").value(mockMember.getNickname()))
			.andExpect(jsonPath("$.data.profileImageUrl").value(mockMember.getProfileImageUrlOrDefaultUrl()))
			.andExpect(jsonPath("$.msg").value("내 정보 조회가 완료되었습니다."));
	}

	@Test
	@DisplayName("리프레시 토큰 없이 요청 시 401 반환")
	void refreshAccessToken_NoToken_Returns401() throws Exception {
		// given
		when(rq.getValueFromCookie("refreshToken")).thenReturn(null);

		// when & then
		mockMvc.perform(get("/api/members/refresh"))
			.andExpect(jsonPath("$.code").value("401"))
			.andExpect(jsonPath("$.msg").value("리프레시 토큰이 제공되지 않았습니다."));
	}

	@Test
	@DisplayName("유효한 리프레시 토큰으로 액세스 토큰 갱신 성공")
	void refreshAccessToken_ValidToken_ReturnsNewAccessToken() throws Exception {
		// given
		String validRefreshToken = "valid-refresh-token";
		String newAccessToken = "new-access-token";

		when(rq.getValueFromCookie("refreshToken")).thenReturn(validRefreshToken);
		when(authService.getMemberByRefreshTokenOrThrow(validRefreshToken)).thenReturn(mockMember);

		when(rq.getRealActor(mockMember)).thenReturn(mockMember);
		when(authService.genAccessToken(any(Member.class))).thenReturn(newAccessToken);

		// when & then
		mockMvc.perform(get("/api/members/refresh"))
			.andExpect(jsonPath("$.code").value("200"))
			.andExpect(jsonPath("$.msg").value("액세스 토큰이 갱신되었습니다."));
	}
}
