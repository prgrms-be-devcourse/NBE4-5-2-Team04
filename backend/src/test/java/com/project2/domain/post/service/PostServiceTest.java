package com.project2.domain.post.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
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
import org.springframework.data.jpa.domain.Specification;

import com.project2.domain.member.entity.Member;
import com.project2.domain.place.entity.Place;
import com.project2.domain.place.repository.PlaceRepository;
import com.project2.domain.post.dto.PostRequestDTO;
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

	// 1. 게시글 생성 테스트
	@Test
	void createPost_shouldCreateNewPost() throws IOException {
		// Given
		PostRequestDTO requestDTO = new PostRequestDTO("Test Title", "Test Content", 1L, 1L, List.of());
		given(rq.getActor()).willReturn(member);
		given(placeRepository.findById(anyLong())).willReturn(Optional.of(place));
		given(postRepository.save(any(Post.class))).willReturn(post);

		// When
		Long postId = postService.createPost(requestDTO);

		// Then
		assertThat(postId).isEqualTo(post.getId());
		verify(postRepository, times(1)).save(any(Post.class));
	}

	// 2. 전체 게시글 조회 테스트
	@Test
	void getPosts_shouldReturnPagedPosts() {
		// Given
		Pageable pageable = Pageable.ofSize(10);
		Page<Post> page = new PageImpl<>(List.of(post));

		// Specification 은 null 을 허용하기 때문에 any()로 처리
		given(postRepository.findAll(any(Specification.class), eq(pageable))).willReturn(page);

		// When
		Page<Post> result = postService.getPosts(null, null, pageable);

		// Then
		assertThat(result.getContent()).hasSize(1);
		assertThat(result.getContent().get(0).getTitle()).isEqualTo("Test Post");
	}

	// 3. 사용자가 좋아요 누른 게시글 조회 테스트
	@Test
	void getLikedPosts_shouldReturnLikedPosts() {
		// Given
		Pageable pageable = Pageable.ofSize(10);
		Page<Post> page = new PageImpl<>(List.of(post));
		given(rq.getActor()).willReturn(member);
		given(postRepository.findLikedPosts(member.getId(), pageable)).willReturn(page);

		// When
		Page<Post> result = postService.getLikedPosts(pageable);

		// Then
		assertThat(result.getContent()).hasSize(1);
	}

	// 4. 사용자가 스크랩한 게시글 조회 테스트
	@Test
	void getScrappedPosts_shouldReturnScrappedPosts() {
		// Given
		Pageable pageable = Pageable.ofSize(10);
		Page<Post> page = new PageImpl<>(List.of(post));
		given(rq.getActor()).willReturn(member);
		given(postRepository.findScrappedPosts(member.getId(), pageable)).willReturn(page);

		// When
		Page<Post> result = postService.getScrappedPosts(pageable);

		// Then
		assertThat(result.getContent()).hasSize(1);
	}

	// 5. 사용자가 팔로우하는 사람들의 게시글 조회 테스트
	@Test
	void getFollowingPosts_shouldReturnFollowingUsersPosts() {
		// Given
		Pageable pageable = Pageable.ofSize(10);
		Page<Post> page = new PageImpl<>(List.of(post));
		given(rq.getActor()).willReturn(member);
		given(postRepository.findFollowingPosts(member.getId(), pageable)).willReturn(page);

		// When
		Page<Post> result = postService.getFollowingPosts(pageable);

		// Then
		assertThat(result.getContent()).hasSize(1);
	}

	// 6. 특정 사용자의 게시글 조회 테스트
	@Test
	void getPostsByMemberId_shouldReturnUserPosts() {
		// Given
		Pageable pageable = Pageable.ofSize(10);
		Page<Post> page = new PageImpl<>(List.of(post));
		given(postRepository.findPostsByMember(1L, pageable)).willReturn(page);

		// When
		Page<Post> result = postService.getPostsByMemberId(1L, pageable);

		// Then
		assertThat(result.getContent()).hasSize(1);
	}

	// 7. 특정 장소의 게시글 조회 테스트
	@Test
	void getPostsByPlaceId_shouldReturnPostsForPlace() {
		// Given
		Pageable pageable = Pageable.ofSize(10);
		Page<Post> page = new PageImpl<>(List.of(post));
		given(postRepository.findPostsByPlace(1L, pageable)).willReturn(page);

		// When
		Page<Post> result = postService.getPostsByPlaceId(1L, pageable);

		// Then
		assertThat(result.getContent()).hasSize(1);
	}

	// 8. 게시글 단건 조회 테스트
	@Test
	void getPostById_shouldReturnPost() {
		// Given
		given(postRepository.findPostById(1L)).willReturn(Optional.of(post));

		// When
		Post result = postService.getPostById(1L);

		// Then
		assertThat(result).isEqualTo(post);
	}

	// 9. 게시글 수정 테스트
	@Test
	void updatePost_shouldUpdatePost() throws IOException, NoSuchAlgorithmException {
		// Given
		PostRequestDTO requestDTO = new PostRequestDTO("Updated Title", "Updated Content", 1L, 1L, List.of());
		given(rq.getActor()).willReturn(member);
		given(postRepository.findById(1L)).willReturn(Optional.of(post));

		// When
		postService.updatePost(1L, requestDTO);

		// Then
		assertThat(post.getTitle()).isEqualTo("Updated Title");
		assertThat(post.getContent()).isEqualTo("Updated Content");
	}

	// 10. 게시글 삭제 테스트
	@Test
	void deletePost_shouldDeletePost() {
		// Given
		given(rq.getActor()).willReturn(member);
		given(postRepository.findById(1L)).willReturn(Optional.of(post));

		// When
		postService.deletePost(1L);

		// Then
		verify(postRepository, times(1)).deleteById(1L);
	}
}