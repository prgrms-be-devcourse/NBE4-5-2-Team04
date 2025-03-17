package com.project2.domain.post.service;

import com.project2.domain.member.dto.FollowRequestDto;
import com.project2.domain.member.dto.FollowResponseDto;
import com.project2.domain.member.entity.Follows;
import com.project2.domain.member.entity.Member;
import com.project2.domain.member.repository.FollowRepository;
import com.project2.domain.member.repository.MemberRepository;
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
	private final FollowRepository followRepository;
	private final MemberRepository memberRepository;

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

	@Transactional
	public RsData<FollowResponseDto> toggleFollow(FollowRequestDto requestDto) {
		Long followerId = requestDto.getFollowerId();
		Long followingId = requestDto.getFollowingId();

		Member follower = memberRepository.findById(followerId)
				.orElseThrow(() -> new IllegalArgumentException("팔로워를 찾을 수 없습니다."));
		Member following = memberRepository.findById(followingId)
				.orElseThrow(() -> new IllegalArgumentException("팔로잉 사용자를 찾을 수 없습니다."));

		boolean isFollowing = followRepository.existsByFollowerAndFollowing(follower, following);

		Follows follows = new Follows(); // Follows 객체 생성

		if (isFollowing) {
			followRepository.deleteByFollowerAndFollowing(follower, following);
		} else {
			follows.setFollower(follower);
			follows.setFollowing(following);
			followRepository.save(follows);
		}

		FollowResponseDto responseDto = new FollowResponseDto(follows); // 생성된 Follows 객체 전달
		return new RsData<>("200", "팔로우 상태 변경 완료", responseDto);
	}
}