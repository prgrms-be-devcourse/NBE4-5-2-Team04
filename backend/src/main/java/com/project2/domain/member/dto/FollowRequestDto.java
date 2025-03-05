package com.project2.domain.member.dto;

import lombok.Getter;
import lombok.Setter;
import com.project2.domain.member.entity.Follows;

@Getter
@Setter
public class FollowRequestDto {
    private Long followerId;
    private Long followingId;

    public FollowRequestDto(Follows follows) {

        this.followerId = follows.getFollower().getId();
        this.followingId = follows.getFollowing().getId();
    }
}
