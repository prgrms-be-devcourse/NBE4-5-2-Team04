package com.project2.domain.post.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.annotation.Transactional;

import com.project2.domain.post.dto.toggle.LikeResponseDTO;
import com.project2.domain.post.dto.toggle.ScrapResponseDTO;
import com.project2.domain.post.entity.Likes;
import com.project2.domain.post.entity.Post;
import com.project2.domain.post.entity.Scrap;
import com.project2.domain.post.mapper.ToggleMapper;
import com.project2.domain.post.repository.LikesRepository;
import com.project2.domain.post.repository.PostRepository;
import com.project2.domain.post.repository.ScrapRepository;
import com.project2.global.dto.RsData;

import jakarta.persistence.EntityNotFoundException;

@ExtendWith(MockitoExtension.class)
@Transactional
class PostToggleServiceTest {

	@InjectMocks
	private PostToggleService postToggleService;

	@Mock
	private LikesRepository likesRepository;

	@Mock
	private ScrapRepository scrapRepository;

	@Mock
	private PostRepository postRepository;

	@Mock
	private ToggleMapper toggleMapper;

	private final Long userId = 1L;
	private final Long postId = 10L;

	@Test
	@DisplayName("좋아요가 없는 경우 추가하고 상태 반환")
	void toggleLikes_addLike() {
		// Given
		when(likesRepository.existsByPostIdAndMemberId(postId, userId)).thenReturn(false);
		when(likesRepository.countByPostId(postId)).thenReturn(1);
		when(toggleMapper.toLikes(userId, postId)).thenReturn(new Likes());

		// When
		RsData<LikeResponseDTO> result = postToggleService.toggleLikes(userId, postId);

		// Then
		assertEquals("200", result.getCode());
		assertEquals("좋아요 상태 변경 완료", result.getMsg());
		assertTrue(result.getData().isLiked());
		assertEquals(1, result.getData().getLikeCount());
		verify(likesRepository).save(any(Likes.class));
	}

	@Test
	@DisplayName("좋아요가 이미 있는 경우 삭제하고 상태 반환")
	void toggleLikes_removeLike() {
		// Given
		when(likesRepository.existsByPostIdAndMemberId(postId, userId)).thenReturn(true);
		when(likesRepository.countByPostId(postId)).thenReturn(0);

		// When
		RsData<LikeResponseDTO> result = postToggleService.toggleLikes(userId, postId);

		// Then
		assertEquals("200", result.getCode());
		assertEquals("좋아요 상태 변경 완료", result.getMsg());
		assertFalse(result.getData().isLiked());
		assertEquals(0, result.getData().getLikeCount());
		verify(likesRepository, never()).save(any(Likes.class));
	}

	@Test
	@DisplayName("스크랩이 없는 경우 추가하고 상태 반환")
	void toggleScrap_addScrap() {
		// Given
		when(scrapRepository.existsByPostIdAndMemberId(postId, userId)).thenReturn(false);
		when(postRepository.getReferenceById(postId)).thenReturn(new Post());
		when(toggleMapper.toScrap(eq(userId), any(Post.class))).thenReturn(new Scrap());
		when(scrapRepository.countByPostId(postId)).thenReturn(1);

		// When
		RsData<ScrapResponseDTO> result = postToggleService.toggleScrap(userId, postId);

		// Then
		assertEquals("200", result.getCode());
		assertEquals("스크랩 상태 변경 완료", result.getMsg());
		assertTrue(result.getData().isScrapped());
		assertEquals(1, result.getData().getScrapCount());
		verify(scrapRepository).save(any(Scrap.class));
	}

	@Test
	@DisplayName("스크랩이 이미 있는 경우 삭제하고 상태 반환")
	void toggleScrap_removeScrap() {
		// Given
		when(scrapRepository.existsByPostIdAndMemberId(postId, userId)).thenReturn(true);
		when(scrapRepository.countByPostId(postId)).thenReturn(0);

		// When
		RsData<ScrapResponseDTO> result = postToggleService.toggleScrap(userId, postId);

		// Then
		assertEquals("200", result.getCode());
		assertEquals("스크랩 상태 변경 완료", result.getMsg());
		assertFalse(result.getData().isScrapped());
		assertEquals(0, result.getData().getScrapCount());
		verify(scrapRepository, never()).save(any(Scrap.class));
	}

	@Test
	@DisplayName("스크랩 추가 시 게시물이 존재하지 않으면 예외 발생")
	void toggleScrap_postNotFound() {
		// Given
		when(scrapRepository.existsByPostIdAndMemberId(postId, userId)).thenReturn(false);
		when(postRepository.getReferenceById(postId)).thenThrow(new EntityNotFoundException("게시물을 찾을 수 없습니다."));

		// When & Then
		EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
			() -> postToggleService.toggleScrap(userId, postId));

		assertEquals("게시물을 찾을 수 없습니다.", exception.getMessage());
	}

	@Test
	@DisplayName("좋아요 상태 조회 - 존재하는 경우")
	void getLikeStatus_exists() {
		// Given
		when(likesRepository.getLikeStatus(postId, userId)).thenReturn(new LikeResponseDTO(true, 5));

		// When
		LikeResponseDTO result = likesRepository.getLikeStatus(postId, userId);

		// Then
		assertTrue(result.isLiked());
		assertEquals(5, result.getLikeCount());
	}

	@Test
	@DisplayName("좋아요 상태 조회 - 존재하지 않는 경우")
	void getLikeStatus_notExists() {
		// Given
		when(likesRepository.getLikeStatus(postId, userId)).thenReturn(new LikeResponseDTO(false, 0));

		// When
		LikeResponseDTO result = likesRepository.getLikeStatus(postId, userId);

		// Then
		assertFalse(result.isLiked());
		assertEquals(0, result.getLikeCount());
	}

	@Test
	@DisplayName("스크랩 상태 조회 - 존재하는 경우")
	void getScrapStatus_exists() {
		// Given
		when(scrapRepository.getScrapStatus(postId, userId)).thenReturn(new ScrapResponseDTO(true, 3));

		// When
		ScrapResponseDTO result = scrapRepository.getScrapStatus(postId, userId);

		// Then
		assertTrue(result.isScrapped());
		assertEquals(3, result.getScrapCount());
	}

	@Test
	@DisplayName("스크랩 상태 조회 - 존재하지 않는 경우")
	void getScrapStatus_notExists() {
		// Given
		when(scrapRepository.getScrapStatus(postId, userId)).thenReturn(new ScrapResponseDTO(false, 0));

		// When
		ScrapResponseDTO result = scrapRepository.getScrapStatus(postId, userId);

		// Then
		assertFalse(result.isScrapped());
		assertEquals(0, result.getScrapCount());
	}
}
