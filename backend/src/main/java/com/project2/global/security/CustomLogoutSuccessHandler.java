package com.project2.global.security;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;

import com.project2.global.dto.RsData;
import com.project2.global.util.Ut;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Component
public class CustomLogoutSuccessHandler implements LogoutSuccessHandler {

	@Value("${custom.url.domain}")
	private String siteDomain;

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
		response.getWriter().write(Ut.Json.toString(
			new RsData("200", "로그아웃이 완료되었습니다.")));
		response.getWriter().flush();
		response.getWriter().close();
	}

	// 쿠키 삭제 메서드
	private void removeCookie(HttpServletResponse response, String cookieName) {
		Cookie cookie = new jakarta.servlet.http.Cookie(cookieName, null);
		cookie.setDomain(siteDomain);
		cookie.setPath("/");
		cookie.setHttpOnly(true);
		cookie.setSecure(true);
		cookie.setMaxAge(0); // 쿠키 즉시 삭제
		cookie.setAttribute("SameSite", "Strict");
		response.addCookie(cookie);
	}
}
