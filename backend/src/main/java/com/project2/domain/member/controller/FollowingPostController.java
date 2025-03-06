package com.project2.domain.member.controller;

import com.project2.domain.member.entity.Member;
import com.project2.domain.member.dto.FollowingPostResponseDto;
import com.project2.domain.member.service.FollowingPostService;
import com.project2.global.dto.RsData;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class FollowingPostController {

    private final FollowingPostService followingPostService;

    @GetMapping("/{userId}/following-posts")
    public RsData<Page<FollowingPostResponseDto>> getFollowingPosts(
            @PathVariable Long userId,
            @AuthenticationPrincipal UserDetails userDetails,
            Pageable pageable) {

        if (!userId.equals(Long.parseLong(userDetails.getUsername()))) {
            throw new IllegalArgumentException("권한이 없습니다.");
        }

        Member user = new Member();
        user.setId(userId);

        Page<FollowingPostResponseDto> followingPosts = followingPostService.getFollowingPosts(user, pageable);

        if (followingPosts.hasContent()) {
            return new RsData<>("200", "Success", followingPosts);
        } else {
            return new RsData<>("204", "No Content", null);
        }
    }
}