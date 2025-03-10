package com.project2.domain.post.controller;

import com.project2.domain.post.dto.toggle.LikeResponseDTO;
import com.project2.domain.post.dto.toggle.ScrapResponseDTO;
import com.project2.domain.post.service.PostToggleService;
import com.project2.global.dto.RsData;
import com.project2.global.security.SecurityUser;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

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
}