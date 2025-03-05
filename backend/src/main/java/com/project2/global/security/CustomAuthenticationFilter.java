package com.project2.global.security;

import com.project2.domain.member.entity.Member;
import com.project2.domain.member.service.MemberService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class CustomAuthenticationFilter extends OncePerRequestFilter {

    private final Rq rq;
    private final MemberService memberService;

    record AuthToken(String accessToken, String refreshToken) {
    }

    private AuthToken getAuthTokenFromRequest() {

        String accessToken = rq.getValueFromCookie("accessToken");
        String refreshToken = rq.getValueFromCookie("refreshToken");

        if (accessToken == null && refreshToken == null) {
            return null;
        }

        return new AuthToken(accessToken, refreshToken);

    }

    private Member getMemberByAccessToken(String accessToken, String refreshToken) {

        Optional<Member> opAccMember = memberService.getMemberByAccessToken(accessToken);

        if (opAccMember.isPresent()) {
            return opAccMember.get();
        }

        Optional<Member> opRefMember = memberService.getMemberByRefreshToken(refreshToken);

        if(opRefMember.isEmpty()) {
            return null;
        }

        String newAccessToken = memberService.genAccessToken(opRefMember.get());

        rq.addCookie("accessToken", newAccessToken);

        return opRefMember.get();
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String url = request.getRequestURI();

        if(List.of("/api/members/oauth-authenticate", "/api/*/members/logout").contains(url)) {
            filterChain.doFilter(request, response);
            return;
        }

        AuthToken tokens = getAuthTokenFromRequest();

        if (tokens == null) {
            filterChain.doFilter(request, response);
            return;
        }

        String accessToken = tokens.accessToken;
        String refreshToken = tokens.refreshToken;

        Member actor = getMemberByAccessToken(accessToken, refreshToken);

        if (actor == null) {
            filterChain.doFilter(request, response);
            return;
        }

        rq.setLogin(actor);
        filterChain.doFilter(request, response);
    }
}