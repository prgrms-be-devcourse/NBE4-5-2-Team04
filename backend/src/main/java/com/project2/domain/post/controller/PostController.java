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

import com.project2.domain.member.entity.Member;
import com.project2.domain.post.dto.PostDetailResponseDTO;
import com.project2.domain.post.dto.PostRequestDTO;
import com.project2.domain.post.dto.PostResponseDTO;
import com.project2.domain.post.entity.Post;
import com.project2.domain.post.service.PostService;
import com.project2.global.dto.RsData;
import com.project2.global.security.Rq;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/posts")
public class PostController {
	private final PostService postService;
	private final Rq rq;

	@PostMapping(consumes = "multipart/form-data")
	public RsData<Long> createPost(@Valid @ModelAttribute PostRequestDTO postRequestDTO) throws IOException {
		Long postID = postService.createPost(postRequestDTO);
		return new RsData<>(String.valueOf(HttpStatus.CREATED.value()), "게시글이 성공적으로 생성되었습니다.", postID);
	}

	// 1. 전체 게시글 조회 (정렬 기준 적용)
	@GetMapping
	public RsData<Page<PostResponseDTO>> getPosts(
		@RequestParam(required = false) String placeName,
		@RequestParam(required = false) String placeCategory,
		@RequestParam(required = false) String placeRegion,
		Pageable pageable
	) {
		Page<Post> posts = postService.getPosts(placeName, placeCategory, placeRegion, pageable);
		return new RsData<>(String.valueOf(HttpStatus.OK.value()), "게시글 조회 성공", posts.map(PostResponseDTO::new));
	}

	// 2. 사용자가 좋아요 누른 게시글 조회
	@GetMapping("/liked")
	public RsData<Page<PostResponseDTO>> getLikedPosts(
		Pageable pageable
	) {
		Page<Post> posts = postService.getLikedPosts(pageable);
		return new RsData<>(String.valueOf(HttpStatus.OK.value()), "게시글 조회 성공", posts.map(PostResponseDTO::new));
	}

	// 3. 사용자가 스크랩한 게시글 조회
	@GetMapping("/scrapped")
	public RsData<Page<PostResponseDTO>> getScrappedPosts(
		Pageable pageable
	) {
		Page<Post> posts = postService.getScrappedPosts(pageable);
		return new RsData<>(String.valueOf(HttpStatus.OK.value()), "게시글 조회 성공", posts.map(PostResponseDTO::new));
	}

	// 4. 사용자의 팔로워들의 게시글 조회
	@GetMapping("/following")
	public RsData<Page<PostResponseDTO>> getFollowerPosts(
		Pageable pageable
	) {
		Page<Post> posts = postService.getFollowingPosts(pageable);
		return new RsData<>(String.valueOf(HttpStatus.OK.value()), "게시글 조회 성공", posts.map(PostResponseDTO::new));
	}

	// 5. 특정 사용자의 게시글 조회
	@GetMapping("/member/{memberId}")
	public RsData<Page<PostResponseDTO>> getPostsByMember(
		@PathVariable("memberId") Long memberId,
		Pageable pageable
	) {
		Page<Post> posts = postService.getPostsByMemberId(memberId, pageable);
		return new RsData<>(String.valueOf(HttpStatus.OK.value()), "게시글 조회 성공", posts.map(PostResponseDTO::new));
	}

	// 6. 특정 사용자의 게시글 조회
	@GetMapping("/place/{placeId}")
	public RsData<Page<PostResponseDTO>> getPostsByPlace(
		@PathVariable("placeId") Long placeId,
		Pageable pageable
	) {
		Page<Post> posts = postService.getPostsByPlaceId(placeId, pageable);
		return new RsData<>(String.valueOf(HttpStatus.OK.value()), "게시글 조회 성공", posts.map(PostResponseDTO::new));
	}

	@GetMapping("/{postId}")
	public RsData<PostDetailResponseDTO> getPostById(@PathVariable Long postId) {
		Member actor = rq.getActor();
		Post post = postService.getPostById(postId);
		return new RsData<>(String.valueOf(HttpStatus.OK.value()), "게시글 조회 성공", new PostDetailResponseDTO(post, actor));
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
