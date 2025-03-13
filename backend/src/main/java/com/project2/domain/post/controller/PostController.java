package com.project2.domain.post.controller;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.project2.domain.place.enums.Category;
import com.project2.domain.place.enums.Region;
import com.project2.domain.post.dto.PostDetailResponseDTO;
import com.project2.domain.post.dto.PostRequestDTO;
import com.project2.domain.post.dto.PostResponseDTO;
import com.project2.domain.post.entity.Post;
import com.project2.domain.post.service.PostService;
import com.project2.global.dto.RsData;
import com.project2.global.security.SecurityUser;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/posts")
public class PostController {
	private final PostService postService;

	@PostMapping
	public RsData<Long> createPost(@Valid @ModelAttribute PostRequestDTO postRequestDTO) throws IOException {
		Long postID = postService.createPost(postRequestDTO);
		return new RsData<>(String.valueOf(HttpStatus.CREATED.value()), "게시글이 성공적으로 생성되었습니다.", postID);
	}

	// 1. 전체 게시글 조회 (정렬 기준 적용)
	@GetMapping
	public RsData<Page<PostResponseDTO>> getPosts(
		@AuthenticationPrincipal SecurityUser actor,
		@RequestParam(required = false) String placeName,
		@RequestParam(required = false) Category category,
		@RequestParam(required = false) Region region,
		Pageable pageable
	) {

		Page<Post> posts = postService.getPosts(placeName, category, region, pageable);
		return new RsData<>(String.valueOf(HttpStatus.OK.value()), "게시글 조회 성공",
			posts.map(post -> new PostResponseDTO(post, actor)));
	}

	// 2. 사용자가 좋아요 누른 게시글 조회
	@GetMapping("/liked")
	public RsData<Page<PostResponseDTO>> getLikedPosts(
		@AuthenticationPrincipal SecurityUser actor,
		Pageable pageable
	) {

		Page<Post> posts = postService.getLikedPosts(actor, pageable);
		return new RsData<>(String.valueOf(HttpStatus.OK.value()), "게시글 조회 성공",
			posts.map(post -> new PostResponseDTO(post, actor)));
	}

	// 3. 사용자가 스크랩한 게시글 조회
	@GetMapping("/scrapped")
	public RsData<Page<PostResponseDTO>> getScrappedPosts(
		@AuthenticationPrincipal SecurityUser actor,
		Pageable pageable
	) {

		Page<Post> posts = postService.getScrappedPosts(actor, pageable);
		return new RsData<>(String.valueOf(HttpStatus.OK.value()), "게시글 조회 성공",
			posts.map(post -> new PostResponseDTO(post, actor)));
	}

	// 4. 사용자의 팔로워들의 게시글 조회
	@GetMapping("/following")
	public RsData<Page<PostResponseDTO>> getFollowerPosts(
		@AuthenticationPrincipal SecurityUser actor,
		Pageable pageable
	) {

		Page<Post> posts = postService.getFollowingPosts(actor, pageable);
		return new RsData<>(String.valueOf(HttpStatus.OK.value()), "게시글 조회 성공",
			posts.map(post -> new PostResponseDTO(post, actor)));
	}

	// 5. 특정 사용자의 게시글 조회
	@GetMapping("/member/{memberId}")
	public RsData<Page<PostResponseDTO>> getPostsByMember(
		@AuthenticationPrincipal SecurityUser actor,
		@PathVariable("memberId") Long memberId,
		Pageable pageable
	) {

		Page<Post> posts = postService.getPostsByMemberId(memberId, pageable);
		return new RsData<>(String.valueOf(HttpStatus.OK.value()), "게시글 조회 성공",
			posts.map(post -> new PostResponseDTO(post, actor)));
	}

	// 6. 특정 장소의 게시글 조회
	@GetMapping("/place/{placeId}")
	public RsData<Page<PostResponseDTO>> getPostsByPlace(
		@AuthenticationPrincipal SecurityUser actor,
		@PathVariable("placeId") Long placeId,
		Pageable pageable
	) {

		Page<Post> posts = postService.getPostsByPlaceId(placeId, pageable);
		return new RsData<>(String.valueOf(HttpStatus.OK.value()), "게시글 조회 성공",
			posts.map(post -> new PostResponseDTO(post, actor)));
	}

	@GetMapping("/{postId}")

	public RsData<PostDetailResponseDTO> getPostById(@PathVariable Long postId,
		@AuthenticationPrincipal SecurityUser actor) {

		Post post = postService.getPostById(postId);
		return new RsData<>(String.valueOf(HttpStatus.OK.value()), "게시글 조회 성공", new PostDetailResponseDTO(post, actor));
	}

	@PutMapping("/{postId}")
	public RsData<Long> updatePost(
		@AuthenticationPrincipal SecurityUser actor,
		@PathVariable Long postId,
		@Valid @ModelAttribute PostRequestDTO postRequestDTO
	) throws IOException, NoSuchAlgorithmException {
		postService.updatePost(actor, postId, postRequestDTO);
		return new RsData<>(String.valueOf(HttpStatus.OK.value()), "게시글이 성공적으로 수정되었습니다.", postId);
	}

	@DeleteMapping("/{postId}")
	public RsData<Void> deletePost(@AuthenticationPrincipal SecurityUser actor, @PathVariable Long postId) {
		postService.deletePost(actor, postId);
		return new RsData<>(String.valueOf(HttpStatus.OK.value()), "게시글이 성공적으로 삭제되었습니다.");
	}

}
