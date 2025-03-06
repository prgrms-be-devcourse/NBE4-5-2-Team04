package com.project2.domain.post.mapper;

import com.project2.domain.member.entity.Member;
import com.project2.domain.post.dto.comment.CommentRequestDTO;
import com.project2.domain.post.dto.comment.CommentResponseDTO;
import com.project2.domain.post.entity.Comment;
import com.project2.domain.post.entity.Post;
import com.project2.global.exception.ServiceException;
import org.springframework.stereotype.Component;

@Component
public class CommentMapper {

    public Comment toEntity(CommentRequestDTO request, Post post, Member member, Comment parent) {
        if (parent != null && parent.getDepth() >= 1) {
            throw new ServiceException("400", "대대댓글은 허용되지 않습니다.");
        }

        int depth = (parent == null) ? 0 : 1;
        return Comment.builder()
                .content(request.getContent())
                .depth(depth)
                .post(post)
                .member(member)
                .parent(parent)
                .build();
    }

    public CommentResponseDTO toResponseDTO(Comment comment) {
        return new CommentResponseDTO(
                comment.getId(),
                comment.getContent(),
                comment.getMember().getNickname()
        );
    }
}
