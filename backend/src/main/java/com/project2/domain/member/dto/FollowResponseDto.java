package com.project2.domain.member.dto;

import lombok.Getter;
import lombok.Setter;
import com.project2.domain.member.entity.Follows;

@Getter
@Setter
public class FollowResponseDto {
    private Long id;
    private Long followerId;
    private Long followingId;

    public FollowResponseDto(Follows follows) {
        this.id = follows.getId();
        this.followerId = follows.getFollower().getId();
        this.followingId = follows.getFollowing().getId();
    }
}