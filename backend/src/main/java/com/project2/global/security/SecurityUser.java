package com.project2.global.security;

import java.util.Collection;
import java.util.Map;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.oauth2.core.user.OAuth2User;

import com.project2.domain.member.entity.Member;

import lombok.Getter;

public class SecurityUser extends User implements OAuth2User {

	@Getter
	private long id;

	public SecurityUser(long id, String email, Collection<? extends GrantedAuthority> authorities) {
		super(email, "", authorities);
		this.id = id;
	}

	public SecurityUser(Member member) {
		this(member.getId(), member.getEmail(), member.getAuthorities());
	}

	@Override
	public <A> A getAttribute(String name) {
		return OAuth2User.super.getAttribute(name);
	}

	@Override
	public Map<String, Object> getAttributes() {
		return Map.of();
	}

	@Override
	public String getName() {
		return this.getUsername();
	}
}
