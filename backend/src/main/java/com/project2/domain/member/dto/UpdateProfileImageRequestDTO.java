package com.project2.domain.member.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UpdateProfileImageRequestDTO {
	private String profileImageUrl;

	public UpdateProfileImageRequestDTO(String profileImageUrl) {
		this.profileImageUrl = profileImageUrl;
	}
}
