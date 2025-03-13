package com.project2.domain.post.mapper;

import org.springframework.stereotype.Component;

import com.project2.domain.member.entity.Member;
import com.project2.domain.post.dto.comment.CommentRequestDTO;
import com.project2.domain.post.dto.comment.CommentResponseDTO;
import com.project2.domain.post.entity.Comment;
import com.project2.domain.post.entity.Post;

@Component
public class CommentMapper {

	public Comment toEntity(CommentRequestDTO request, Post post, Member member, Comment parent) {
		int depth = (parent == null) ? 0 : 1;
		return Comment.builder()
			.content(request.getContent())
			.depth(depth)
			.post(post)
			.member(member)
			.parent(parent)
			.build();
	}

	public CommentResponseDTO toResponseDTO(Comment comment, String nickname) {
		return new CommentResponseDTO(
			comment.getId(),
			comment.getContent(),
			nickname,
			comment.getParent() != null ? comment.getParent().getId() : null
		);
	}

}