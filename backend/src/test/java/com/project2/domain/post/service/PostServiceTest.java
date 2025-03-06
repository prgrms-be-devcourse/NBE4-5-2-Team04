package com.project2.domain.post.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
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
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
	private Pageable pageable;

	@BeforeEach
	void setUp() {
		member = new Member();
		place = new Place();
		pageable = PageRequest.of(0, 10, Sort.by("createdDate").descending());

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
	@DisplayName("좋아요 순 정렬 테스트")
	void getPostsSortedByLikes() {
		// Given
		List<PostResponseDTO> postList = List.of(
			new PostResponseDTO(1L, "Title 1", "Content 1", 37.5665, 126.9780, 10L, 2L, "img1.jpg",
				LocalDateTime.now(), LocalDateTime.now()),
			new PostResponseDTO(2L, "Title 2", "Content 2", 37.5665, 126.9780, 8L, 5L, "img1.jpg",
				LocalDateTime.now(), LocalDateTime.now())
		);
		Page<PostResponseDTO> postPage = new PageImpl<>(postList, pageable, postList.size());

		when(postRepository.findAllOrderBySorted(eq("likes"), any(Pageable.class))).thenReturn(postPage);

		// When
		Page<PostResponseDTO> result = postService.getPosts("likes", pageable);

		// Then
		assertThat(result).isNotNull();
		assertThat(result.getTotalElements()).isEqualTo(2);
		assertThat(result.getContent().get(0).getLikeCount()).isGreaterThan(result.getContent().get(1).getLikeCount());

		verify(postRepository, times(1)).findAllOrderBySorted(eq("likes"), any(Pageable.class));
	}

	@Test
	@DisplayName("스크랩 순 정렬 테스트")
	void getPostsSortedByScrap() {
		// Given
		List<PostResponseDTO> postList = List.of(
			new PostResponseDTO(3L, "Title 3", "Content 3", 37.5665, 126.9780, 5L, 10L, "img3.jpg",
				LocalDateTime.now(), LocalDateTime.now()),
			new PostResponseDTO(4L, "Title 4", "Content 4", 37.5665, 126.9780, 3L, 8L, "img3.jpg",
				LocalDateTime.now(), LocalDateTime.now())
		);
		Page<PostResponseDTO> postPage = new PageImpl<>(postList, pageable, postList.size());

		when(postRepository.findAllOrderBySorted(eq("scrap"), any(Pageable.class))).thenReturn(postPage);

		// When
		Page<PostResponseDTO> result = postService.getPosts("scrap", pageable);

		// Then
		assertThat(result).isNotNull();
		assertThat(result.getTotalElements()).isEqualTo(2);
		assertThat(result.getContent().get(0).getScrapCount()).isGreaterThan(
			result.getContent().get(1).getScrapCount());

		verify(postRepository, times(1)).findAllOrderBySorted(eq("scrap"), any(Pageable.class));
	}

	@Test
	@DisplayName("게시글이 없을 경우 빈 페이지 반환")
	void getPostsReturnsEmptyPage() {
		// Given
		Page<PostResponseDTO> emptyPage = Page.empty(pageable);
		when(postRepository.findAllOrderBySorted(anyString(), any(Pageable.class))).thenReturn(emptyPage);

		// When
		Page<PostResponseDTO> result = postService.getPosts("likes", pageable);

		// Then
		assertThat(result).isNotNull();
		assertThat(result.getTotalElements()).isEqualTo(0);
		assertThat(result.getContent()).isEmpty();

		verify(postRepository, times(1)).findAllOrderBySorted(anyString(), any(Pageable.class));
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
		postService.updatePost(1L, postRequestDTO);

		// Then
		verify(postRepository, times(1)).findById(1L);  // 게시글 조회 확인
		verify(postImageService, times(1)).updateImages(post, postRequestDTO.getImages()); // 이미지 업데이트 확인
		assertThat(post.getTitle()).isEqualTo("Updated Title"); // 객체가 올바르게 변경되었는지 확인
		assertThat(post.getContent()).isEqualTo("Updated Content");
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