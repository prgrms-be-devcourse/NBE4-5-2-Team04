package com.project2.domain.post.controller;

import com.project2.domain.member.dto.FollowRequestDto;
import com.project2.domain.member.dto.FollowResponseDto;
import com.project2.domain.post.dto.toggle.LikeResponseDTO;
import com.project2.domain.post.dto.toggle.ScrapResponseDTO;
import com.project2.domain.post.service.PostToggleService;
import com.project2.global.dto.RsData;
import com.project2.global.security.SecurityUser;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/posts")
public class PostToggleController {

    private final PostToggleService postToggleService;

    @PostMapping("/{postId}/likes")
    public RsData<LikeResponseDTO> toggleLike(@AuthenticationPrincipal SecurityUser securityUser, @PathVariable Long postId) {
        return postToggleService.toggleLikes(securityUser.getId(), postId);
    }

    @PostMapping("/{postId}/scraps")
    public RsData<ScrapResponseDTO> toggleScrap(@AuthenticationPrincipal SecurityUser securityUser, @PathVariable Long postId) {
        return postToggleService.toggleScrap(securityUser.getId(), postId);
    }

    @PostMapping("/{postId}/follow")
    public RsData<FollowResponseDto> toggleFollow(
            @AuthenticationPrincipal SecurityUser securityUser,
            @PathVariable Long postId,
            @RequestBody FollowRequestDto requestDto
    ) {
        // 현재 로그인한 사용자의 ID를 followerId로 설정
        requestDto.setFollowerId(securityUser.getId());
        // followingId는 requestDto에 포함되어 있어야 함
        return postToggleService.toggleFollow(requestDto);
    }

}