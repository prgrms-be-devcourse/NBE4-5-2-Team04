package com.project2.domain.post.service;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Collections;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project2.domain.member.entity.Member;
import com.project2.domain.place.entity.Place;
import com.project2.domain.place.repository.PlaceRepository;
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
	public PostResponseDTO createPost(PostRequestDTO requestDTO) throws IOException {
		Member actor = rq.getActor();

		Place place = placeRepository.findById(requestDTO.getPlaceId())
			.orElseThrow(() -> new IllegalArgumentException("해당 장소가 존재하지 않음"));
		Post post = Post.builder()
			.title(requestDTO.getTitle())
			.content(requestDTO.getContent())
			.latitude(requestDTO.getLatitude())
			.longitude(requestDTO.getLongitude())
			.place(place)
			.member(actor)
			.build();
		postRepository.save(post);

		List<String> imageUrls = null;
		if (requestDTO.getImages() != null && !requestDTO.getImages().isEmpty()) {
			imageUrls = postImageService.saveImages(post, requestDTO.getImages(), Collections.emptyList());
		}

		return new PostResponseDTO(post, imageUrls);
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
	public PostResponseDTO updatePost(Long postId, PostRequestDTO requestDTO) throws
		IOException,
		NoSuchAlgorithmException {
		Member actor = rq.getActor();

		Post post = postRepository.findById(postId)
			.orElseThrow(() -> new IllegalArgumentException("해당 게시글이 존재하지 않습니다."));

		if (!post.getMember().equals(actor)) {
			throw new ServiceException(String.valueOf(HttpStatus.FORBIDDEN.value()), "게시글 수정 권한이 없습니다.");
		}

		post.update(requestDTO.getTitle(), requestDTO.getContent());

		List<String> newImages = postImageService.updateImages(post, requestDTO.getImages());

		return new PostResponseDTO(post, newImages);
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
