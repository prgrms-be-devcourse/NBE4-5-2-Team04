package com.project2.domain.post.service;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project2.domain.member.entity.Member;
import com.project2.domain.place.entity.Place;
import com.project2.domain.place.repository.PlaceRepository;
import com.project2.domain.post.dto.PostDetailResponseDTO;
import com.project2.domain.post.dto.PostRequestDTO;
import com.project2.domain.post.dto.PostResponseDTO;
import com.project2.domain.post.entity.Post;
import com.project2.domain.post.repository.PostRepository;
import com.project2.global.exception.ServiceException;
import com.project2.global.security.Rq;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PostService {
	private final PostRepository postRepository;
	private final PlaceRepository placeRepository;
	private final PostImageService postImageService;
	private final Rq rq;

	@Transactional(rollbackFor = Exception.class)
	public Long createPost(PostRequestDTO requestDTO) throws IOException {
		Member actor = rq.getActor();

		Place place = placeRepository.findById(requestDTO.getPlaceId())
			.orElseThrow(() -> new IllegalArgumentException("해당 장소가 존재하지 않음"));

		Post post = Post.builder()
			.title(requestDTO.getTitle())
			.content(requestDTO.getContent())
			.place(place)
			.member(actor)
			.build();
		Post createdPost = postRepository.save(post);

		if (requestDTO.getImages() != null && !requestDTO.getImages().isEmpty()) {
			postImageService.saveImages(post, requestDTO.getImages(), Collections.emptyList());
		}

		return createdPost.getId();
	}

	// 1. 전체 게시글 조회 (정렬 기준 적용)
	@Transactional(readOnly = true)
	public Page<PostResponseDTO> getPosts(String sortBy, String placeName, String placeCategory, Pageable pageable) {
		Page<Object[]> results = postRepository.findAllBySearchWordsAndSort(sortBy, placeName, placeCategory, pageable);
		return results.map(this::mapToPostResponseDTO);
	}

	// 2. 사용자가 좋아요 누른 게시글 조회
	@Transactional(readOnly = true)
	public Page<PostResponseDTO> getLikedPosts(Pageable pageable) {
		Member member = rq.getActor();
		Page<Object[]> results = postRepository.findLikedPosts(member.getId(), pageable);
		return results.map(this::mapToPostResponseDTO);
	}

	// 3. 사용자가 스크랩한 게시글 조회
	@Transactional(readOnly = true)
	public Page<PostResponseDTO> getScrappedPosts(Pageable pageable) {
		Member member = rq.getActor();
		Page<Object[]> results = postRepository.findScrappedPosts(member.getId(), pageable);
		return results.map(this::mapToPostResponseDTO);
	}

	// 4. 사용자의 팔로워들의 게시글 조회
	@Transactional(readOnly = true)
	public Page<PostResponseDTO> getFollowerPosts(Pageable pageable) {
		Member member = rq.getActor();
		Page<Object[]> results = postRepository.findFollowerPosts(member.getId(), pageable);
		return results.map(this::mapToPostResponseDTO);
	}

	// 5. 특정 사용자의 게시글 조회
	@Transactional(readOnly = true)
	public Page<PostResponseDTO> getPostsByMemberId(Long targetMemberId, Pageable pageable) {
		Page<Object[]> results = postRepository.findPostsByMember(targetMemberId, pageable);
		return results.map(this::mapToPostResponseDTO);
	}

	@Transactional(readOnly = true)
	public Page<PostResponseDTO> getPostsByPlaceId(Long placeId, Pageable pageable) {
		Page<Object[]> results = postRepository.findPostsByPlace(placeId, pageable);
		return results.map(this::mapToPostResponseDTO);
	}

	@Transactional(readOnly = true)
	public PostDetailResponseDTO getPostById(Long postId) {
		Member actor = rq.getActor();
		Optional<Object[]> result = postRepository.findPostDetailById(postId, actor.getId());
		return result.map(this::mapToPostDetailResponseDTO)
			.orElseThrow(() -> new IllegalArgumentException("해당 게시글이 존재하지 않습니다."));
	}

	@Transactional
	public void updatePost(Long postId, PostRequestDTO requestDTO) throws
		IOException,
		NoSuchAlgorithmException {
		Member actor = rq.getActor();

		Post post = postRepository.findById(postId)
			.orElseThrow(() -> new IllegalArgumentException("해당 게시글이 존재하지 않습니다."));

		if (!post.getMember().equals(actor)) {
			throw new ServiceException(String.valueOf(HttpStatus.FORBIDDEN.value()), "게시글 수정 권한이 없습니다.");
		}

		post.update(requestDTO.getTitle(), requestDTO.getContent());

		postImageService.updateImages(post, requestDTO.getImages());
	}

	@Transactional
	public void deletePost(Long postId) {
		Member actor = rq.getActor();

		Post post = postRepository.findById(postId)
			.orElseThrow(() -> new IllegalArgumentException("해당 게시글이 존재하지 않습니다."));

		if (!post.getMember().equals(actor)) {
			throw new ServiceException(String.valueOf(HttpStatus.FORBIDDEN.value()), "게시글 삭제 권한이 없습니다.");
		}
		postRepository.deleteById(postId);
	}

	private PostResponseDTO mapToPostResponseDTO(Object[] obj) {
		return new PostResponseDTO(
			((Number)obj[0]).longValue(), (String)obj[1], (String)obj[2],
			(String)obj[3], (String)obj[4], ((Number)obj[5]).intValue(),
			((Number)obj[6]).intValue(), ((Number)obj[7]).intValue(),
			(String)obj[8],
			((Number)obj[9]).longValue(), (String)obj[10], (String)obj[11]
		);
	}

	private PostDetailResponseDTO mapToPostDetailResponseDTO(Object[] obj) {
		return new PostDetailResponseDTO(
			((Number)obj[0]).longValue(), // id
			(String)obj[1], // title
			(String)obj[2], // content
			((Number)obj[3]).longValue(), // memberId
			(String)obj[4], // nickname
			(String)obj[5], // profileImageUrl
			(String)obj[6], // placeName
			(String)obj[7], // category
			((Number)obj[8]).intValue(), // likeCount
			((Number)obj[9]).intValue(), // scrapCount
			(obj[10] != null && (Boolean)obj[10]), // isLiked
			(obj[11] != null && (Boolean)obj[11]), // isScrapped
			obj[12] != null ? (String)obj[12] : "", // imageUrls (null 방어)
			(LocalDateTime)obj[13], // createdDate
			(LocalDateTime)obj[14] // modifiedDate
		);
	}
}
