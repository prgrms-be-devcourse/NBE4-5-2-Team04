package com.project2.domain.post.controller;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.project2.domain.post.dto.PostDetailResponseDTO;
import com.project2.domain.post.dto.PostRequestDTO;
import com.project2.domain.post.dto.PostResponseDTO;
import com.project2.domain.post.service.PostService;
import com.project2.global.dto.RsData;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/posts")
public class PostController {
	private final PostService postService;

	@PostMapping(consumes = "multipart/form-data")
	public RsData<Long> createPost(@Valid @ModelAttribute PostRequestDTO postRequestDTO) throws IOException {
		Long postID = postService.createPost(postRequestDTO);
		return new RsData<>(String.valueOf(HttpStatus.CREATED.value()), "게시글이 성공적으로 생성되었습니다.", postID);
	}

	// 1. 전체 게시글 조회 (정렬 기준 적용)
	@GetMapping
	public Page<PostResponseDTO> getPosts(
		@RequestParam(required = false, defaultValue = "createdDate") String sortBy,
		@RequestParam(required = false) String placeName,
		@RequestParam(required = false) String placeCategory,
		Pageable pageable
	) {
		return postService.getPosts(sortBy, placeName, placeCategory, pageable);
	}

	// 2. 사용자가 좋아요 누른 게시글 조회
	@GetMapping("/liked")
	public Page<PostResponseDTO> getLikedPosts(
		Pageable pageable
	) {
		return postService.getLikedPosts(pageable);
	}

	// 3. 사용자가 스크랩한 게시글 조회
	@GetMapping("/scrapped")
	public Page<PostResponseDTO> getScrappedPosts(
		Pageable pageable
	) {
		return postService.getScrappedPosts(pageable);
	}

	// 4. 사용자의 팔로워들의 게시글 조회
	@GetMapping("/followers")
	public Page<PostResponseDTO> getFollowerPosts(
		Pageable pageable
	) {
		return postService.getFollowerPosts(pageable);
	}

	// 5. 특정 사용자의 게시글 조회
	@GetMapping("/member/{memberId}")
	public Page<PostResponseDTO> getPostsByMember(
		@PathVariable("memberId") Long memberId,
		Pageable pageable
	) {
		return postService.getPostsByMember(memberId, pageable);
	}

	@GetMapping("/{postId}")
	public RsData<PostDetailResponseDTO> getPostById(@PathVariable Long postId) {
		PostDetailResponseDTO post = postService.getPostById(postId);
		return new RsData<>(String.valueOf(HttpStatus.OK.value()), "게시글 조회 성공", post);
	}

	@PutMapping("/{postId}")
	public RsData<Void> updatePost(
		@PathVariable Long postId,
		@Valid @ModelAttribute PostRequestDTO postRequestDTO
	) throws IOException, NoSuchAlgorithmException {
		postService.updatePost(postId, postRequestDTO);
		return new RsData<>(String.valueOf(HttpStatus.OK.value()), "게시글이 성공적으로 수정되었습니다.");
	}

	@DeleteMapping("/{postId}")
	public RsData<Void> deletePost(@PathVariable Long postId) {
		postService.deletePost(postId);
		return new RsData<>(String.valueOf(HttpStatus.OK.value()), "게시글이 성공적으로 삭제되었습니다.");
	}

}
