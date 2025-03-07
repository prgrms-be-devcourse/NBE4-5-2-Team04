package com.project2.domain.member.dto;


import com.project2.domain.post.entity.Post;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class FollowingPostResponseDto{
    private Long postId;
    private String title;
    private String content;
    private Long userId;
    private String nickname;
    private String profileImageUrl;
    private LocalDateTime createdAt;

    public static FollowingPostResponseDto fromEntity(Post post) {
        return new FollowingPostResponseDto(
                post.getId(),
                post.getTitle(),
                post.getContent(),
                post.getMember().getId(),
                post.getMember().getNickname(),
                post.getMember().getProfileImageUrl(),
                post.getCreatedDate()
        );
    }
}

