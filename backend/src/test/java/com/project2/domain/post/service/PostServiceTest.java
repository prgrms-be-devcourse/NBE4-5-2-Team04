package com.project2.domain.post.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import com.project2.domain.member.entity.Member;
import com.project2.domain.place.entity.Place;
import com.project2.domain.place.repository.PlaceRepository;
import com.project2.domain.post.dto.PostDetailResponseDTO;
import com.project2.domain.post.dto.PostRequestDTO;
import com.project2.domain.post.dto.PostResponseDTO;
import com.project2.domain.post.entity.Post;
import com.project2.domain.post.repository.PostRepository;
import com.project2.global.security.Rq;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {

	@InjectMocks
	private PostService postService;

	@Mock
	private PostRepository postRepository;

	@Mock
	private PlaceRepository placeRepository;

	@Mock
	private PostImageService postImageService;

	@Mock
	private Rq rq;

	private Member member;
	private Place place;
	private Post post;

	@BeforeEach
	void setUp() {
		member = Member.builder().id(1L).nickname("testUser").build();
		place = Place.builder().id(1L).name("Seoul").build();
		post = Post.builder()
			.id(1L)
			.title("Test Post")
			.content("Test Content")
			.member(member)
			.place(place)
			.build();
	}

	@Test
	void post_success() throws IOException {
		// Given
		PostRequestDTO requestDTO = new PostRequestDTO("Test Title", "Test Content", 1L, 1L, List.of());
		given(rq.getActor()).willReturn(member);
		given(placeRepository.findById(anyLong())).willReturn(Optional.of(place));
		given(postRepository.save(any(Post.class))).willReturn(post);

		// When
		Long postId = postService.createPost(requestDTO);

		// Then
		assertThat(postId).isEqualTo(post.getId());
	}

	@Test
	void getPosts_success() {
		// Given
		List<Object[]> data = new ArrayList<>();
		data.add(new Object[] {
			1L, "Test Post", "Test Content", "Seoul", "City", 10, 5, 3, "img1.jpg,img2.jpg", 2L, "testUser",
			"profile.png"
		});
		Page<Object[]> mockPage = new PageImpl<>(data);
		given(postRepository.findAllBySearchWordsAndSort(anyString(), anyString(), anyString(), any(Pageable.class)))
			.willReturn(mockPage);

		// When
		Page<PostResponseDTO> result = postService.getPosts("likes", "Seoul", "City", Pageable.unpaged());

		// Then
		assertThat(result.getTotalElements()).isEqualTo(1);
		assertThat(result.getContent().get(0).getTitle()).isEqualTo("Test Post");
	}

	@Test
	void getLikedPosts_success() {
		// Given
		given(rq.getActor()).willReturn(member);
		List<Object[]> data = new ArrayList<>();
		data.add(new Object[] {
			1L, "Test Post", "Test Content", "Seoul", "City", 10, 5, 3, "img1.jpg,img2.jpg", 2L, "testUser",
			"profile.png"
		});
		Page<Object[]> mockPage = new PageImpl<>(data);
		given(postRepository.findLikedPosts(anyLong(), any(Pageable.class))).willReturn(mockPage);

		// When
		Page<PostResponseDTO> result = postService.getLikedPosts(Pageable.unpaged());

		// Then
		assertThat(result.getTotalElements()).isEqualTo(1);
	}

	@Test
	void getScrappedPosts_success() {
		// Given
		given(rq.getActor()).willReturn(member);
		List<Object[]> data = new ArrayList<>();
		data.add(new Object[] {
			1L, "Test Post", "Test Content", "Seoul", "City", 10, 5, 3, "img1.jpg,img2.jpg", 2L, "testUser",
			"profile.png"
		});
		Page<Object[]> mockPage = new PageImpl<>(data);
		given(postRepository.findScrappedPosts(anyLong(), any(Pageable.class))).willReturn(mockPage);

		// When
		Page<PostResponseDTO> result = postService.getScrappedPosts(Pageable.unpaged());

		// Then
		assertThat(result.getTotalElements()).isEqualTo(1);
	}

	@Test
	void getPostsByMemberId_success() {
		// Given
		List<Object[]> data = new ArrayList<>();
		data.add(new Object[] {
			1L, "Test Post", "Test Content", "Seoul", "City", 10, 5, 3, "img1.jpg,img2.jpg", 2L, "testUser",
			"profile.png"
		});
		Page<Object[]> mockPage = new PageImpl<>(data);
		given(postRepository.findPostsByMember(anyLong(), any(Pageable.class))).willReturn(mockPage);

		// When
		Page<PostResponseDTO> result = postService.getPostsByMemberId(1L, Pageable.unpaged());

		// Then
		assertThat(result.getTotalElements()).isEqualTo(1);
	}

	@Test
	void getPost_success() {
		// Given
		given(rq.getActor()).willReturn(member);
		Object[] mockResult = {
			1L, "Test Post", "Test Content", 2L, "testUser", "profile.png",
			"Seoul", "City", 10, 5, true, false, "img1.jpg,img2.jpg",
			LocalDateTime.now(), LocalDateTime.now()
		};
		given(postRepository.findPostDetailById(anyLong(), anyLong())).willReturn(Optional.of(mockResult));

		// When
		PostDetailResponseDTO result = postService.getPostById(1L);

		// Then
		assertThat(result.getTitle()).isEqualTo("Test Post");
	}

	@Test
	void postDelete_success() {
		// Given
		given(rq.getActor()).willReturn(member);
		given(postRepository.findById(anyLong())).willReturn(Optional.of(post));

		// When
		postService.deletePost(1L);

		// Then
		verify(postRepository, times(1)).deleteById(1L);
	}
}