package com.project2.global.security;

import com.project2.domain.member.entity.Member;
import com.project2.domain.member.service.AuthService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final Rq rq;
    private final AuthService authService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        HttpSession session = request.getSession();

        String redirectUrl = (String)session.getAttribute("redirectUrl");
        if(redirectUrl == null) {
            redirectUrl = "http://localhost:3000";
        }
        session.removeAttribute("redirectUrl");

        Member member = rq.getActor();
        String accessToken = authService.genAccessToken(member);
        String refreshToken = authService.genRefreshToken(member);

        rq.addCookie("accessToken", accessToken);
        rq.addCookie("refreshToken", refreshToken);

        response.sendRedirect(redirectUrl);
    }
}