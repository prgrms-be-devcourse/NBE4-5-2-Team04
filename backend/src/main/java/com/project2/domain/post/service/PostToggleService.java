package com.project2.domain.post.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project2.domain.post.dto.toggle.LikeResponseDTO;
import com.project2.domain.post.dto.toggle.ScrapResponseDTO;
import com.project2.domain.post.entity.Post;
import com.project2.domain.post.mapper.ToggleMapper;
import com.project2.domain.post.repository.LikesRepository;
import com.project2.domain.post.repository.PostRepository;
import com.project2.domain.post.repository.ScrapRepository;
import com.project2.global.dto.RsData;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PostToggleService {

	private final LikesRepository likesRepository;
	private final ScrapRepository scrapRepository;
	private final PostRepository postRepository;
	private final ToggleMapper toggleMapper;

	@Transactional
	public RsData<LikeResponseDTO> toggleLikes(Long userId, Long postId) {
		boolean isLiked = likesRepository.existsByPostIdAndMemberId(postId, userId);

		if (isLiked) {
			likesRepository.toggleLikeIfExists(postId, userId);
		} else {
			likesRepository.save(toggleMapper.toLikes(userId, postId));
		}

		LikeResponseDTO responseDTO = new LikeResponseDTO(!isLiked, likesRepository.countByPostId(postId));
		return new RsData<>("200", "좋아요 상태 변경 완료", responseDTO);
	}

	@Transactional
	public RsData<ScrapResponseDTO> toggleScrap(Long userId, Long postId) {
		boolean isScrapped = scrapRepository.existsByPostIdAndMemberId(postId, userId);

		if (isScrapped) {
			scrapRepository.toggleScrapIfExists(postId, userId);
		} else {
			Post post = postRepository.getReferenceById(postId);
			scrapRepository.save(toggleMapper.toScrap(userId, post));
		}

		ScrapResponseDTO responseDTO = new ScrapResponseDTO(!isScrapped, scrapRepository.countByPostId(postId));
		return new RsData<>("200", "스크랩 상태 변경 완료", responseDTO);
	}
}