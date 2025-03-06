package com.project2.domain.post.dto.comment;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CommentResponseDTO {
    private Long id;
    private String content;
    private String nickname; // 작성자 정보만 제공
}