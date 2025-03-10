package com.project2.domain.post.service;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Collections;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project2.domain.member.entity.Member;
import com.project2.domain.place.entity.Place;
import com.project2.domain.place.repository.PlaceRepository;
import com.project2.domain.post.dto.PostRequestDTO;
import com.project2.domain.post.entity.Post;
import com.project2.domain.post.repository.PostRepository;
import com.project2.domain.post.specification.PostSpecification;
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
	public Page<Post> getPosts(String placeName, String category, Pageable pageable) {
		// 동적 검색 적용
		Specification<Post> spec = PostSpecification.filterByPlaceAndCategory(placeName, category);
		return postRepository.findAll(spec, pageable);
	}

	// 2. 사용자가 좋아요 누른 게시글 조회
	@Transactional(readOnly = true)
	public Page<Post> getLikedPosts(Pageable pageable) {
		Member member = rq.getActor();
		return postRepository.findLikedPosts(member.getId(), pageable);
	}

	// 3. 사용자가 스크랩한 게시글 조회
	@Transactional(readOnly = true)
	public Page<Post> getScrappedPosts(Pageable pageable) {
		Member member = rq.getActor();
		return postRepository.findScrappedPosts(member.getId(), pageable);
	}

	// 4. 사용자의 팔로워들의 게시글 조회
	@Transactional(readOnly = true)
	public Page<Post> getFollowingPosts(Pageable pageable) {
		Member member = rq.getActor();
		return postRepository.findFollowingPosts(member.getId(), pageable);
	}

	// 5. 특정 사용자의 게시글 조회
	@Transactional(readOnly = true)
	public Page<Post> getPostsByMemberId(Long targetMemberId, Pageable pageable) {
		return postRepository.findPostsByMember(targetMemberId, pageable);
	}

	@Transactional(readOnly = true)
	public Page<Post> getPostsByPlaceId(Long placeId, Pageable pageable) {
		return postRepository.findPostsByPlace(placeId, pageable);
	}

	@Transactional(readOnly = true)
	public Post getPostById(Long postId) {
		return postRepository.findPostById(postId)
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
}