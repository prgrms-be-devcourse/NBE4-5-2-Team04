package com.project2.domain.member.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FollowResponseDto {
    private Long id;
    private Long followerId;
    private Long followingId;
}