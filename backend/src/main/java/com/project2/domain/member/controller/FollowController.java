package com.project2.domain.member.controller;

import com.project2.domain.member.dto.FollowRequestDto;
import com.project2.domain.member.dto.FollowResponseDto;
import com.project2.domain.member.dto.FollowerResponseDto;
import com.project2.domain.member.service.FollowService;
import com.project2.domain.member.service.FollowerService;
import com.project2.domain.member.service.FollowingService;

import com.project2.domain.post.dto.PostResponseDTO;

import com.project2.domain.post.entity.Post;
import com.project2.domain.post.service.PostService;
import com.project2.global.dto.RsData;
import com.project2.global.exception.ServiceException;
import com.project2.global.security.Rq;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
public class FollowController {

    private final FollowService followService;
    private final FollowerService followerService;
    private final FollowingService followingService;
    private final PostService postService;


    @PostMapping("/{memberid}/follows")
    public RsData<FollowResponseDto> toggleFollow(
            @PathVariable Long memberid,
            @RequestBody FollowRequestDto requestDto
    ) {
            requestDto.setFollowerId(memberid);
            return followService.toggleFollow(requestDto);
        }


    @GetMapping("/{memberId}/followers")
    public ResponseEntity<RsData<List<FollowerResponseDto>>> getFollowers(@PathVariable Long memberId) {
        try {
            List<FollowerResponseDto> followers = followerService.getFollowers(memberId);


            if (followers.isEmpty()) {
                return ResponseEntity.ok(
                        new RsData<>(
                                "204",
                                "팔로워가 없습니다.",
                                null
                        )
                );
            }

            return ResponseEntity.ok(
                    new RsData<>(
                            "200",
                            "팔로워 목록이 성공적으로 조회되었습니다.",
                            followers
                    )
            );
        } catch (ServiceException e) {
            return ResponseEntity
                    .status(e.getStatusCode())
                    .body(
                            new RsData<>(
                                    e.getCode(),
                                    e.getMsg(),
                                    null
                            )
                    );
        }
    }

    @GetMapping("/{memberId}/followings")
    public ResponseEntity<RsData<List<FollowerResponseDto>>> getFollowings(@PathVariable Long memberId) {
        try {
            List<FollowerResponseDto> followings = followingService.getFollowings(memberId);

            // Check if the list of followings is empty
            if (followings.isEmpty()) {
                return ResponseEntity.ok(
                        new RsData<>(
                                "204",
                                "팔로잉이 없습니다.",
                                null
                        )
                );
            }

            return ResponseEntity.ok(
                    new RsData<>(
                            "200",
                            "팔로잉 목록이 성공적으로 조회되었습니다.",
                            followings
                    )
            );
        } catch (ServiceException e) {
            return ResponseEntity
                    .status(e.getStatusCode())
                    .body(
                            new RsData<>(
                                    e.getCode(),
                                    e.getMsg(),
                                    null
                            )
                    );
        }
    }


    @GetMapping("/{memberId}/following-posts")
    public Page<Post> getFollowingPosts(
            @PathVariable Long memberId,
            Pageable pageable
    ) {
        // memberId를 사용하여 PostService의 메서드를 호출

        return postService.getFollowingPosts(pageable);

    }
}