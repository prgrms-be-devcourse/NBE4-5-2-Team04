package com.project2.global.security;

import com.project2.domain.member.entity.Member;
import com.project2.domain.member.service.AuthService;
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
    private final AuthService authService;

    private boolean isAuthorizationHeader() {
        String authorizationHeader = rq.getHeader("Authorization");

        if (authorizationHeader == null) {
            return false;
        }

        return authorizationHeader.trim().startsWith("Bearer ");
    }

    record AuthToken(String accessToken, String refreshToken) {
    }

    private AuthToken getAuthTokenFromRequest() {

        if (isAuthorizationHeader()) {
            String authorizationHeader = rq.getHeader("Authorization");
            String accessToken = authorizationHeader.substring("Bearer ".length());

            // Bearer 토큰이 사용되면 refreshToken은 빈 값("")으로 설정
            return new AuthToken(accessToken, "");
        }

        String accessToken = rq.getValueFromCookie("accessToken");
        String refreshToken = rq.getValueFromCookie("refreshToken");

        if (accessToken == null && refreshToken == null) {
            return null;
        }

        return new AuthToken(accessToken, refreshToken);

    }

    private Member getMemberByAccessToken(String accessToken, String refreshToken) {

        Optional<Member> opAccMember = authService.getMemberByAccessToken(accessToken);
        if (opAccMember.isPresent()) {
            return opAccMember.get();
        }

        // refreshToken이 비어있으면 DB 조회하지 않음
        if (refreshToken == null || refreshToken.isEmpty()) {
            return null;
        }

        Optional<Member> opRefMember = authService.getMemberByRefreshToken(refreshToken);

        if(opRefMember.isEmpty()) {
            return null;
        }

        String newAccessToken = authService.genAccessToken(opRefMember.get());
        rq.addCookie("accessToken", newAccessToken, false);

        return opRefMember.get();
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {


        String url = request.getRequestURI();

        if(List.of("/api/members/logout", "/api/members/login").contains(url)) {
            filterChain.doFilter(request, response);
            return;
        }

        AuthToken tokens = getAuthTokenFromRequest();

        if (tokens == null || (tokens.accessToken.isEmpty() && tokens.refreshToken.isEmpty())) {
            filterChain.doFilter(request, response);
            return;
        }

        String accessToken = tokens.accessToken;

        String refreshToken = tokens.refreshToken;

        Member actor = getMemberByAccessToken(accessToken, refreshToken);

        if (actor == null) {
            response.setContentType("application/json;charset=UTF-8");
            response.setCharacterEncoding("UTF-8");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"message\": \"토큰이 만료되었습니다.\"}");
            response.getWriter().flush();
            return;
        }
        rq.setLogin(actor);
        filterChain.doFilter(request, response);
    }
}