package com.project2.domain.member.entity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.project2.domain.member.enums.Provider;
import com.project2.global.entity.BaseTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
public class Member extends BaseTime {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, unique = true)
	private String email;

	@Column(nullable = false, length = 50)
	private String nickname;

	private String profileImageUrl;

	@Enumerated(EnumType.STRING)
	@Column(columnDefinition = "VARCHAR(20)")
	private Provider provider;

	public Collection<? extends GrantedAuthority> getAuthorities() {
		return getMemberAuthoritiesAsString()
			.stream()
			.map(SimpleGrantedAuthority::new)
			.toList();
	}

	public List<String> getMemberAuthoritiesAsString() {

		return new ArrayList<>();
	}

	public String getProfileImageUrlOrDefaultUrl() {
		return (profileImageUrl == null || profileImageUrl.isBlank()) ? "https://placehold.co/640x640?text=O_O" : this.profileImageUrl;
	}
}


