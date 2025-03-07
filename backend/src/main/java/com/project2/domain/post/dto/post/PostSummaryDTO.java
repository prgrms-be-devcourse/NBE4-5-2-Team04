package com.project2.domain.post.dto.post;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PostSummaryDTO {
    private Long postId;
    private String title;
    private String imageUrl;
    private int likeCount;
    private int commentCount;
    private String createdAt;
    private AuthorInfo author;

    @Getter
    @Builder
    public static class AuthorInfo {
        private Long userId;
        private String nickname;
        private String profileImageUrl;
    }
}