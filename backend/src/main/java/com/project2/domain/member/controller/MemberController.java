package com.project2.domain.member.controller;

import com.project2.domain.member.dto.OAuthAuthenticationResponseDto;
import com.project2.domain.member.dto.OAuthAuthenticationRequestDto;
import com.project2.domain.member.entity.Member;
import com.project2.domain.member.service.MemberService;
import com.project2.global.dto.RsData;
import com.project2.global.security.Rq;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/members")
public class MemberController {

    private final MemberService memberService;
    private final Rq rq;

    @PostMapping("/oauth-authenticate")
    public RsData<OAuthAuthenticationResponseDto> oauthAuthenticate(@RequestBody OAuthAuthenticationRequestDto oAuthAuthenticationRequestDto) {
        Member member = memberService.loginOrSignUp(oAuthAuthenticationRequestDto.getEmail()
                , oAuthAuthenticationRequestDto.getNickname()
                , oAuthAuthenticationRequestDto.getAuthCode()
                , oAuthAuthenticationRequestDto.getProvider());

        String accessToken = memberService.genAccessToken(member);
        rq.addCookie("accessToken", accessToken);

        String refreshToken = memberService.genRefreshToken(member);
        rq.addCookie("refreshToken", refreshToken);

        return new RsData<>(
                "201",
                "인증이 완료되었습니다.",
                new OAuthAuthenticationResponseDto(member)
        );
    }
}
