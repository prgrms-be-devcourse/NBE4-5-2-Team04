package com.project2.domain.member.controller;

import com.project2.domain.member.dto.FollowerResponseDto;
import com.project2.domain.member.entity.Member;
import com.project2.domain.member.enums.Provider;
import com.project2.domain.member.service.AuthService;
import com.project2.domain.member.service.FollowerService;
import com.project2.domain.member.service.FollowingService;
import com.project2.domain.member.service.MemberService;
import com.project2.global.security.Rq;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class MemberControllerTest {

    @InjectMocks
    private MemberController memberController;
    @Mock
    private MemberService memberService;
    @Mock
    private FollowerService followerService;
    @Mock
    private FollowingService followingService;

    @Mock
    private Rq rq;

    @Mock
    private AuthService authService;

    private MockMvc mockMvc;
    private Member mockMember;

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

    @Test
    @DisplayName("회원 ID를 이용하여 사용자 프로필 정보를 성공적으로 조회한다.")
    void getUserProfile_ValidMemberId_ReturnsProfileData() throws Exception {
        // given
        long memberId = mockMember.getId();
        List<FollowerResponseDto> followers = List.of(FollowerResponseDto.fromEntity(new Member())
                , FollowerResponseDto.fromEntity(new Member())
                , FollowerResponseDto.fromEntity(new Member()));
        List<FollowerResponseDto> followings = List.of(FollowerResponseDto.fromEntity(new Member())
                , FollowerResponseDto.fromEntity(new Member())
                , FollowerResponseDto.fromEntity(new Member()));

        // when
        when(memberService.findByIdOrThrow(memberId)).thenReturn(mockMember);
        when(followerService.getFollowers(memberId)).thenReturn(followers);
        when(followingService.getFollowings(memberId)).thenReturn(followings);

        // then
        mockMvc.perform(get("/api/members/" + memberId))
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.msg").value("사용자 프로필 조회가 완료되었습니다."));
    }
}
