package com.project2.domain.member.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.project2.domain.member.entity.Follows;

@Getter
@Setter
@NoArgsConstructor
public class FollowRequestDto {
    private Long followerId;
    private Long followingId;

//    public FollowRequestDto(Follows follows) {
//
//        this.followerId = follows.getFollower().getId();
//        this.followingId = follows.getFollowing().getId();
//    }
}
