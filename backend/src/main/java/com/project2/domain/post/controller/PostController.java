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
import org.springframework.web.bind.annotation.RestController;

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
	public RsData<PostResponseDTO> createPost(@Valid @ModelAttribute PostRequestDTO postRequestDTO) throws IOException {
		PostResponseDTO post = postService.createPost(postRequestDTO);
		return new RsData<>(String.valueOf(HttpStatus.CREATED.value()), "게시글이 성공적으로 생성되었습니다.", post);
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
		@Valid @ModelAttribute PostRequestDTO postRequestDTO
	) throws IOException, NoSuchAlgorithmException {
		PostResponseDTO post = postService.updatePost(postId, postRequestDTO);
		return new RsData<>(String.valueOf(HttpStatus.OK.value()), "게시글이 성공적으로 수정되었습니다.", post);
	}

	@DeleteMapping("/{postId}")
	public RsData<Void> deletePost(@PathVariable Long postId) {
		postService.deletePost(postId);
		return new RsData<>(String.valueOf(HttpStatus.OK.value()), "게시글이 성공적으로 삭제되었습니다.");
	}

}
