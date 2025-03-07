package com.project2.global.security;

import com.project2.global.app.AppConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

	private final CustomAuthenticationFilter customAuthenticationFilter;
	private final CustomAuthorizationRequestResolver customAuthorizationRequestResolver;
	private final CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler;
	private final CustomLogoutSuccessHandler customLogoutSuccessHandler;

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
			.authorizeHttpRequests(auth -> auth
				.requestMatchers("/h2-console/**", "/auth/**", "/oauth2/**", "/v3/api-docs",
						"/api/members/login", "/api/members/logout").permitAll()
				.anyRequest().authenticated() // 모든 요청에 대해 인증 필요하도록 변경
			)
			.csrf(AbstractHttpConfigurer::disable)
			.oauth2Login(oauth2 -> {
				oauth2.authorizationEndpoint(
						authorizationEndpoint -> authorizationEndpoint
								.authorizationRequestResolver(customAuthorizationRequestResolver)
				);
				oauth2.successHandler(customAuthenticationSuccessHandler);
			})
			.addFilterBefore(customAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
			.logout(logout -> logout
					.logoutUrl("/api/members/logout") // 로그아웃 URL 지정
					.logoutSuccessHandler(customLogoutSuccessHandler)
			)
			.headers(headers -> headers
			.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable));

		return http.build();
	}

	@Bean
	public UrlBasedCorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration configuration = new CorsConfiguration();
		// 허용할 오리진 설정
		configuration.setAllowedOrigins(Arrays.asList("https://cdpn.io", AppConfig.getSiteFrontUrl()));
		// 허용할 HTTP 메서드 설정
		configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE"));
		// 자격 증명 허용 설정
		configuration.setAllowCredentials(true);
		// 허용할 헤더 설정
		configuration.setAllowedHeaders(Arrays.asList("*"));
		// CORS 설정을 소스에 등록
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/api/**", configuration);

		return source;
	}
}