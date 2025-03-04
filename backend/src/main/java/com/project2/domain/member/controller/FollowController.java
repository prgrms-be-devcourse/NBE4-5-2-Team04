package com.project2.domain.member.controller;

import com.project2.domain.member.dto.FollowRequestDto;
import com.project2.domain.member.dto.FollowResponseDto;
import com.project2.domain.member.entity.Member;
import com.project2.domain.member.service.FollowService;
import com.project2.global.dto.RsData;
import com.project2.global.exception.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/follows")
public class FollowController {

    private final FollowService followService;

    @Autowired
    public FollowController(FollowService followService) {
        this.followService = followService;
    }

    @PostMapping
    public ResponseEntity<RsData<FollowResponseDto>> toggleFollow(
            @AuthenticationPrincipal Member follower, // @AuthenticationPrincipal 추가
            @RequestBody FollowRequestDto requestDto
    ) {
        try {
            FollowResponseDto responseDto = followService.toggleFollow(follower, requestDto); // follower 전달

            if (responseDto == null) {
                return ResponseEntity.ok(
                        new RsData<>(
                                "204",
                                "언팔로우 되었습니다.",
                                null
                        )
                );
            }

            return ResponseEntity.ok(
                    new RsData<>(
                            "200",
                            "팔로우 되었습니다.",
                            responseDto
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
}