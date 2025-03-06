package com.project2.global.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomLogoutSuccessHandler implements LogoutSuccessHandler {

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
            throws IOException, ServletException {

        // 세션 무효화
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }

        // SecurityContext 초기화
        SecurityContextHolder.clearContext();

        // 쿠키 삭제
        removeCookie(response, "accessToken");
        removeCookie(response, "refreshToken");
        removeCookie(response, "JSESSIONID");

        // 로그아웃 성공 응답 반환
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().write("{\"code\": \"200\", \"msg\": \"로그아웃 되었습니다.\"}");
        response.getWriter().flush();
        response.getWriter().close();
    }

    // 쿠키 삭제 메서드
    private void removeCookie(HttpServletResponse response, String cookieName) {
        Cookie cookie = new jakarta.servlet.http.Cookie(cookieName, null);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setMaxAge(0); // 쿠키 즉시 삭제
        response.addCookie(cookie);
    }
}
