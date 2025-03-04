package com.project2.domain.post.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project2.domain.member.entity.Member;
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

	@PostMapping
	public RsData<PostResponseDTO> createPost(@Valid @RequestBody PostRequestDTO postRequestDTO,
		@AuthenticationPrincipal Member member) {
		PostResponseDTO responseDto = postService.createPost(postRequestDTO, member);
		return new RsData<>(String.valueOf(HttpStatus.CREATED.value()), "게시글이 성공적으로 생성되었습니다.", responseDto);
	}

	@GetMapping
	public RsData<Page<PostResponseDTO>> getPosts(Pageable pageable) {
		Page<PostResponseDTO> posts = postService.getPosts(pageable);
		return new RsData<>(String.valueOf(HttpStatus.OK.value()), "게시글 목록 조회 성공", posts);
	}

	@GetMapping("/{postId}")
	public RsData<PostResponseDTO> getPostById(@PathVariable Long postId) {
		PostResponseDTO post = postService.getPostById(postId);
		return new RsData<>(String.valueOf(HttpStatus.OK.value()), "게시글 조회 성공", post);
	}

	@PutMapping("/{postId}")
	public RsData<PostResponseDTO> updatePost(
		@PathVariable Long postId,
		@Valid @RequestBody PostRequestDTO postRequestDTO
	) {
		PostResponseDTO updatedPost = postService.updatePost(postId, postRequestDTO);
		return new RsData<>(String.valueOf(HttpStatus.OK.value()), "게시글이 성공적으로 수정되었습니다.", updatedPost);
	}

	@DeleteMapping("/{postId}")
	public RsData<Void> deletePost(@PathVariable Long postId) {
		postService.deletePost(postId);
		return new RsData<>(String.valueOf(HttpStatus.OK.value()), "게시글이 성공적으로 삭제되었습니다.");
	}

}
