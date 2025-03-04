package com.project2.domain.member.entity;

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

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Member extends BaseTime {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "member_id")
	private Long id;

	@Column(nullable = false, unique = true)
	private String email;

	@Column(nullable = false, length = 50)
	private String nickname;

	private String profileImageUrl;

	@Enumerated(EnumType.STRING)
	@Column(columnDefinition = "VARCHAR(20)")
	private Provider provider;

}


