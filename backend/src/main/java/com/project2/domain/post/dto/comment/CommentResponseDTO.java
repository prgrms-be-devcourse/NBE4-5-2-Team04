package com.project2.domain.post.dto.comment;

import org.springframework.lang.NonNull;

import lombok.Getter;

@Getter
public class CommentResponseDTO {
	@NonNull
	private final Long id;

	@NonNull
	private final String content;

	@NonNull
	private final String nickname;

	@NonNull
	private final Long parentId;

	public CommentResponseDTO(Long id, String content, String nickname, Long parentId) {
		this.id = id;
		this.content = content;
		this.nickname = nickname;
		this.parentId = parentId;
	}
}