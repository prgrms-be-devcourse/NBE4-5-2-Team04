package com.project2.global.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
			.authorizeHttpRequests(auth -> auth
				.requestMatchers("/h2-console/**").permitAll()
				.anyRequest().permitAll()
			)
			.csrf(AbstractHttpConfigurer::disable)
			.headers(headers -> headers
				.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable)
			);

		return http.build();
	}
}