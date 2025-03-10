package com.project2.domain.post.service;

import com.project2.domain.post.dto.toggle.LikeResponseDTO;
import com.project2.domain.post.dto.toggle.ScrapResponseDTO;
import com.project2.domain.post.entity.Post;
import com.project2.domain.post.mapper.ToggleMapper;
import com.project2.domain.post.repository.LikesRepository;
import com.project2.domain.post.repository.PostRepository;
import com.project2.domain.post.repository.ScrapRepository;
import com.project2.global.dto.RsData;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PostToggleService {

    private final LikesRepository likesRepository;
    private final ScrapRepository scrapRepository;
    private final PostRepository postRepository;
    private final ToggleMapper toggleMapper;

    @Transactional
    public RsData<LikeResponseDTO> toggleLikes(Long userId, Long postId) {
        if (likesRepository.toggleLikeIfExists(postId, userId) > 0) {
            return new RsData<>("200", "좋아요가 취소되었습니다.", likesRepository.getLikeStatus(postId, userId));
        }

        likesRepository.save(toggleMapper.toLikes(userId, postId));
        return new RsData<>("200", "좋아요가 추가되었습니다.", likesRepository.getLikeStatus(postId, userId));
    }

    @Transactional
    public RsData<ScrapResponseDTO> toggleScrap(Long userId, Long postId) {
        if (scrapRepository.toggleScrapIfExists(postId, userId) == 0) {
            Post post = postRepository.getReferenceById(postId);
            scrapRepository.save(toggleMapper.toScrap(userId, post));
        }

        return new RsData<>("200", "스크랩 상태 변경 완료", scrapRepository.getScrapStatus(postId, userId));
    }
}