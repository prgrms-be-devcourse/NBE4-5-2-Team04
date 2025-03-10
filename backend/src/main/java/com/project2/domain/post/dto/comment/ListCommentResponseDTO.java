package com.project2.domain.post.dto.comment;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class ListCommentResponseDTO {
    private final Long id;
    private final String content;
    private final String nickname;
    private final Long parentId;
    private final List<ListCommentResponseDTO> children = new ArrayList<>();

    public ListCommentResponseDTO(Long id, String content, String nickname, Long parentId) {
        this.id = id;
        this.content = content;
        this.nickname = nickname;
        this.parentId = parentId;
    }
}