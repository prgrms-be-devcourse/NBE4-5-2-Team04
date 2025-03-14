package com.project2.global.security;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.context.NullSecurityContextRepository;
import org.springframework.security.web.header.writers.frameoptions.XFrameOptionsHeaderWriter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.project2.global.dto.RsData;
import com.project2.global.util.Ut;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

	private final CustomAuthenticationFilter customAuthenticationFilter;
	private final CustomAuthorizationRequestResolver customAuthorizationRequestResolver;
	private final CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler;
	private final CustomLogoutSuccessHandler customLogoutSuccessHandler;

	@Value("${custom.url.front-url}")
	private String siteFrontUrl;

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http.authorizeHttpRequests(
				auth -> auth.requestMatchers("/h2-console/**", "/auth/**", "/oauth2/**", "/v3/api-docs/**",
						"/api/members/login", "/api/members/logout", "/login", "/error",
						"/uploads/**", "/_next/image", "/swagger-ui/**")
					.permitAll()
					.anyRequest()
					.authenticated() // 모든 요청에 대해 인증 필요하도록 변경
			)
			.csrf(AbstractHttpConfigurer::disable)
			.sessionManagement(sessionManagement -> {
				sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS);
			})
			.securityContext(
				securityContext -> securityContext.securityContextRepository(new NullSecurityContextRepository()))
			.oauth2Login(oauth2 -> {
				oauth2.authorizationEndpoint(
					authorizationEndpoint -> authorizationEndpoint.authorizationRequestResolver(
						customAuthorizationRequestResolver));
				oauth2.successHandler(customAuthenticationSuccessHandler);
			})
			.addFilterBefore(customAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
			.logout(logout -> logout.logoutUrl("/api/members/logout") // 로그아웃 URL 지정
				.logoutSuccessHandler(customLogoutSuccessHandler))
			.headers((headers) -> headers.addHeaderWriter(
				new XFrameOptionsHeaderWriter(XFrameOptionsHeaderWriter.XFrameOptionsMode.SAMEORIGIN)))
			.exceptionHandling(
				exceptionHandling -> exceptionHandling.authenticationEntryPoint((request, response, authException) -> {
					response.setContentType("application/json;charset=UTF-8");
					response.setStatus(401);
					response.getWriter().write(Ut.Json.toString(new RsData("401-1", "잘못된 인증키입니다.")));
				}).accessDeniedHandler((request, response, authException) -> {
					response.setContentType("application/json;charset=UTF-8");
					response.setStatus(403);
					response.getWriter().write(Ut.Json.toString(new RsData("403-1", "접근 권한이 없습니다.")));
				}));
		;

		return http.build();
	}

	@Bean
	public UrlBasedCorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration configuration = new CorsConfiguration();
		// 허용할 오리진 설정
		configuration.setAllowedOrigins(Arrays.asList("https://cdpn.io", siteFrontUrl));
		// 허용할 HTTP 메서드 설정
		configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE"));
		// 자격 증명 허용 설정
		configuration.setAllowCredentials(true);
		// 허용할 헤더 설정
		configuration.setAllowedHeaders(Arrays.asList("*"));
		// CORS 설정을 소스에 등록
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/api/**", configuration);
		source.registerCorsConfiguration("/uploads/**", configuration);

		return source;
	}
}