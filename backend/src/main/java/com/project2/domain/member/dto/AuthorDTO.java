package com.project2.domain.member.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AuthorDTO {
	private Long memberId;
	private String nickname;
	private String profileImageUrl;
}
