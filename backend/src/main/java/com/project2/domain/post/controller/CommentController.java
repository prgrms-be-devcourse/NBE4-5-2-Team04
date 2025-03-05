package com.project2.domain.post.controller;

import com.project2.domain.post.dto.comment.CommentRequestDTO;
import com.project2.domain.post.dto.comment.CommentResponseDTO;
import com.project2.domain.post.service.CommentService;
import com.project2.global.dto.Empty;
import com.project2.global.dto.RsData;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public RsData<List<CommentResponseDTO>> getComments(@PathVariable Long postId) {
        return commentService.getComments(postId);
    }

    @PutMapping("/comments/{commentId}")
    public RsData<CommentResponseDTO> updateComment(@PathVariable Long commentId, @RequestBody CommentRequestDTO request) {
        return commentService.updateComment(commentId, request);
    }

    @DeleteMapping("/comments/{commentId}")
    public RsData<Empty> deleteComment(@PathVariable Long commentId) {
        return commentService.deleteComment(commentId);
    }
}