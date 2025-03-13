package com.project2.domain.post.controller;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project2.domain.post.dto.comment.CommentRequestDTO;
import com.project2.domain.post.dto.comment.CommentResponseDTO;
import com.project2.domain.post.dto.comment.ListCommentResponseDTO;
import com.project2.domain.post.service.CommentService;
import com.project2.global.dto.Empty;
import com.project2.global.dto.RsData;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CommentController {
	private final CommentService commentService;

	@PostMapping("/posts/{postId}/comments")
	public RsData<CommentResponseDTO> createComment(@PathVariable Long postId, @RequestBody CommentRequestDTO request) {
		return commentService.createComment(postId, request);
	}

	@GetMapping("/posts/{postId}/comments")
	public RsData<List<ListCommentResponseDTO>> getComments(@PathVariable Long postId) {
		return commentService.getComments(postId);
	}

	@PutMapping("/comments/{commentId}")
	public RsData<CommentResponseDTO> updateComment(@PathVariable Long commentId,
		@RequestBody CommentRequestDTO request) {
		return commentService.updateComment(commentId, request);
	}

	@DeleteMapping("/comments/{commentId}")
	public RsData<Empty> deleteComment(@PathVariable Long commentId) {
		return commentService.deleteComment(commentId);
	}
}