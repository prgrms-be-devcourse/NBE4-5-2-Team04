package com.project2.domain.member.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UpdateNicknameDTO {
	@NotBlank
	private String nickname;

	public UpdateNicknameDTO(String nickname) {
		this.nickname = nickname;
	}
}
