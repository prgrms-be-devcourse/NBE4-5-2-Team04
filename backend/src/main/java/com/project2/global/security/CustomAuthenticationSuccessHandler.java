package com.project2.global.security;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.project2.domain.member.entity.Member;
import com.project2.domain.member.service.AuthService;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

	private final Rq rq;
	private final AuthService authService;

	@Value("${custom.url.front-url}")
	private String siteFrontUrl;

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
		Authentication authentication) throws IOException, ServletException {
		HttpSession session = request.getSession();

		String redirectUrl = (String)session.getAttribute("redirectUrl");
		if (redirectUrl == null) {
			redirectUrl = siteFrontUrl;
		}
		session.removeAttribute("redirectUrl");

		Member member = rq.getActor();
		String accessToken = authService.genAccessToken(member);
		String refreshToken = authService.genRefreshToken(member);

		rq.addCookie("accessToken", accessToken, false);
		rq.addCookie("refreshToken", refreshToken, true);

		response.sendRedirect(redirectUrl);
	}
}