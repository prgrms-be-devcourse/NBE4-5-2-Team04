package com.project2.domain.post.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project2.domain.member.entity.Member;
import com.project2.domain.post.dto.PostRequestDTO;
import com.project2.domain.post.dto.PostResponseDTO;
import com.project2.domain.post.entity.Post;
import com.project2.domain.post.repository.PostRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PostService {
	private final PostRepository postRepository;

	@Transactional
	public PostResponseDTO createPost(PostRequestDTO requestDTO, Member member) {
		Post post = Post.builder()
			.title(requestDTO.getTitle())
			.content(requestDTO.getContent())
			.latitude(requestDTO.getLatitude())
			.longitude(requestDTO.getLongitude())
			.member(member)
			.build();
		postRepository.save(post);
		return new PostResponseDTO(post);
	}

	@Transactional(readOnly = true)
	public Page<PostResponseDTO> getPosts(Pageable pageable) {
		return postRepository.findAll(pageable).map(PostResponseDTO::new);
	}

	@Transactional(readOnly = true)
	public PostResponseDTO getPostById(Long postId) {
		Post post = postRepository.findById(postId)
			.orElseThrow(() -> new IllegalArgumentException("해당 게시글이 존재하지 않습니다."));
		return new PostResponseDTO(post);
	}

	@Transactional
	public PostResponseDTO updatePost(Long postId, PostRequestDTO requestDTO) {
		Post post = postRepository.findById(postId)
			.orElseThrow(() -> new IllegalArgumentException("해당 게시글이 존재하지 않습니다."));
		post.update(requestDTO.getTitle(), requestDTO.getContent());
		return new PostResponseDTO(post);
	}

	@Transactional
	public void deletePost(Long postId) {
		if (!postRepository.existsById(postId)) {
			throw new IllegalArgumentException("해당 게시글이 존재하지 않습니다.");
		}
		postRepository.deleteById(postId);
	}
}
