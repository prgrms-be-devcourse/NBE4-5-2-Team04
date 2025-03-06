package com.project2.domain.member.controller;

import com.project2.domain.member.dto.FollowRequestDto;
import com.project2.domain.member.dto.FollowResponseDto;
import com.project2.domain.member.dto.FollowerResponseDto;
import com.project2.domain.member.entity.Member;
import com.project2.domain.member.service.FollowService;
import com.project2.domain.member.service.FollowerService;
import com.project2.domain.member.service.FollowingService;
import com.project2.global.dto.RsData;
import com.project2.global.exception.ServiceException;
import com.project2.global.security.Rq;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class FollowController {

    private final FollowService followService;
    private final FollowerService followerService;
    private final FollowingService followingService;
    private final Rq rq;


    @PostMapping("/{userid}/follows")
    public ResponseEntity<RsData<FollowResponseDto>> toggleFollow(
            @PathVariable Long userid,
            @RequestBody FollowRequestDto requestDto
    ) {
        try {
            Member actor = rq.getActor(); // 현재 인증된 사용자 정보

            // actor의 ID와 userid가 동일한지 확인
            if (!actor.getId().equals(userid)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(new RsData<>(
                                "403",
                                "자신을 팔로우하거나 언팔로우할 수 없습니다.",
                                null
                        ));
            }


            FollowResponseDto responseDto = followService.toggleFollow(actor.getId(), requestDto);

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

    @GetMapping("/{userId}/followers")
    public ResponseEntity<RsData<List<FollowerResponseDto>>> getFollowers(@PathVariable Long userId) {
        try {
            List<FollowerResponseDto> followers = followerService.getFollowers(userId);

            // Check if the list of followers is empty
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
    @GetMapping("/{userId}/followings")
    public ResponseEntity<RsData<List<FollowerResponseDto>>> getFollowings(@PathVariable Long userId) {
        try {
            List<FollowerResponseDto> followings = followingService.getFollowings(userId);

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
}
