package com.project2.domain.post.service;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Collections;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project2.domain.member.entity.Member;
import com.project2.domain.member.repository.MemberRepository;
import com.project2.domain.place.entity.Place;
import com.project2.domain.place.repository.PlaceRepository;
import com.project2.domain.post.dto.PostRequestDTO;
import com.project2.domain.post.dto.PostResponseDTO;
import com.project2.domain.post.entity.Post;
import com.project2.domain.post.repository.PostRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PostService {
	private final PostRepository postRepository;
	private final MemberRepository memberRepository;
	private final PlaceRepository placeRepository;
	private final PostImageService postImageService;

	@Transactional(rollbackFor = Exception.class)
	public PostResponseDTO createPost(PostRequestDTO requestDTO, Member member2) throws IOException {
		// TODO jwt 되면 member2 를 사용하도록 변경
		Member member = memberRepository.findById(requestDTO.getMemberId()).get();
		Place place = placeRepository.findById(requestDTO.getPlaceId())
			.orElseThrow(() -> new IllegalArgumentException("해당 장소가 존재하지 않음"));
		Post post = Post.builder()
			.title(requestDTO.getTitle())
			.content(requestDTO.getContent())
			.latitude(requestDTO.getLatitude())
			.longitude(requestDTO.getLongitude())
			.place(place)
			.member(member)
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
		Post post = postRepository.findById(postId)
			.orElseThrow(() -> new IllegalArgumentException("해당 게시글이 존재하지 않습니다."));
		post.update(requestDTO.getTitle(), requestDTO.getContent());

		List<String> newImages = postImageService.updateImages(post, requestDTO.getImages());

		return new PostResponseDTO(post, newImages);
	}

	@Transactional
	public void deletePost(Long postId) {
		if (!postRepository.existsById(postId)) {
			throw new IllegalArgumentException("해당 게시글이 존재하지 않습니다.");
		}
		postRepository.deleteById(postId);
	}
}
