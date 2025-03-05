package com.project2.domain.post.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.multipart.MultipartFile;

import com.project2.domain.member.entity.Member;
import com.project2.domain.place.entity.Place;
import com.project2.domain.place.repository.PlaceRepository;
import com.project2.domain.post.dto.PostRequestDTO;
import com.project2.domain.post.dto.PostResponseDTO;
import com.project2.domain.post.entity.Post;
import com.project2.domain.post.repository.PostRepository;
import com.project2.global.exception.ServiceException;
import com.project2.global.security.Rq;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {

	@Mock
	private PostRepository postRepository;

	@Mock
	private PlaceRepository placeRepository;

	@Mock
	private PostImageService postImageService;

	@Mock
	private Rq rq;

	@InjectMocks
	private PostService postService;

	private Member member;
	private Place place;
	private Post post;
	private PostRequestDTO postRequestDTO;

	@BeforeEach
	void setUp() {
		member = new Member();
		place = new Place();

		post = Post.builder()
			.title("Test Title")
			.content("Test Content")
			.latitude(37.5665)
			.longitude(126.9780)
			.place(place)
			.member(member)
			.build();

		postRequestDTO = new PostRequestDTO();
		postRequestDTO.setTitle("Updated Title");
		postRequestDTO.setContent("Updated Content");
		postRequestDTO.setLatitude(37.1234);
		postRequestDTO.setLongitude(127.5678);
		postRequestDTO.setPlaceId(1L);
		postRequestDTO.setImages(List.of(mock(MultipartFile.class)));
	}

	@Test
	@DisplayName("게시글 생성 성공")
	void createPost_Success() throws IOException {
		// Given
		when(rq.getActor()).thenReturn(member);
		when(placeRepository.findById(anyLong())).thenReturn(Optional.of(place));
		when(postRepository.save(any(Post.class))).thenReturn(post);
		when(postImageService.saveImages(any(Post.class), anyList(), anyList())).thenReturn(Collections.emptyList());

		// When
		PostResponseDTO response = postService.createPost(postRequestDTO);

		// Then
		assertThat(response).isNotNull();
		assertThat(response.getTitle()).isEqualTo("Updated Title");
		verify(postRepository, times(1)).save(any(Post.class));
		verify(postImageService, times(1)).saveImages(any(Post.class), anyList(), anyList());
	}

	@Test
	@DisplayName("게시글 목록 조회 성공")
	void getPosts_Success() {
		// Given
		PageRequest pageable = PageRequest.of(0, 10);
		Page<Post> postPage = new PageImpl<>(List.of(post));
		when(postRepository.findAll(pageable)).thenReturn(postPage);

		// When
		Page<PostResponseDTO> response = postService.getPosts("likes", pageable);

		// Then
		assertThat(response).isNotEmpty();
		assertThat(response.getContent().get(0).getTitle()).isEqualTo("Test Title");
	}

	@Test
	@DisplayName("게시글 상세 조회 성공")
	void getPostById_Success() {
		// Given
		when(postRepository.findById(anyLong())).thenReturn(Optional.of(post));

		// When
		PostResponseDTO response = postService.getPostById(1L);

		// Then
		assertThat(response).isNotNull();
		assertThat(response.getTitle()).isEqualTo("Test Title");
	}

	@Test
	@DisplayName("게시글 조회 실패 - 존재하지 않는 게시글")
	void getPostById_ShouldThrowException_WhenPostDoesNotExist() {
		// Given
		when(postRepository.findById(anyLong())).thenReturn(Optional.empty());

		// When & Then
		assertThatThrownBy(() -> postService.getPostById(1L))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage("해당 게시글이 존재하지 않습니다.");
	}

	@Test
	@DisplayName("게시글 수정 성공")
	void updatePost_Success() throws IOException, NoSuchAlgorithmException {
		// Given
		when(rq.getActor()).thenReturn(member);
		when(postRepository.findById(anyLong())).thenReturn(Optional.of(post));
		when(postImageService.updateImages(any(Post.class), anyList())).thenReturn(Collections.emptyList());

		// When
		PostResponseDTO response = postService.updatePost(1L, postRequestDTO);

		// Then
		assertThat(response).isNotNull();
		assertThat(response.getTitle()).isEqualTo("Updated Title");
	}

	@Test
	@DisplayName("게시글 수정 실패 - 권한 없음")
	void updatePost_ShouldThrowException_WhenUnauthorized() {
		// Given
		Member anotherMember = new Member();
		post.setMember(anotherMember);
		when(rq.getActor()).thenReturn(member);
		when(postRepository.findById(anyLong())).thenReturn(Optional.of(post));

		// When & Then
		assertThatThrownBy(() -> postService.updatePost(1L, postRequestDTO))
			.isInstanceOf(ServiceException.class)
			.hasMessage("게시글 수정 권한이 없습니다.")
			.extracting("code")
			.isEqualTo(String.valueOf(HttpStatus.FORBIDDEN.value()));
	}

	@Test
	@DisplayName("게시글 삭제 성공")
	void deletePost_Success() {
		// Given
		when(rq.getActor()).thenReturn(member);
		when(postRepository.findById(anyLong())).thenReturn(Optional.of(post));

		// When
		postService.deletePost(1L);

		// Then
		verify(postRepository, times(1)).deleteById(1L);
	}

	@Test
	@DisplayName("게시글 삭제 실패 - 권한 없음")
	void deletePost_ShouldThrowException_WhenUnauthorized() {
		// Given
		Member anotherMember = new Member();
		post.setMember(anotherMember);
		when(rq.getActor()).thenReturn(member);
		when(postRepository.findById(anyLong())).thenReturn(Optional.of(post));

		// When & Then
		assertThatThrownBy(() -> postService.deletePost(1L))
			.isInstanceOf(ServiceException.class)
			.hasMessage("게시글 삭제 권한이 없습니다.")
			.extracting("code")
			.isEqualTo(String.valueOf(HttpStatus.FORBIDDEN.value()));
	}
}