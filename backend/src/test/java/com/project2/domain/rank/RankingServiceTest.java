package com.project2.domain.rank;

import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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

import com.project2.domain.member.entity.Member;
import com.project2.domain.place.entity.Place;
import com.project2.domain.place.enums.Category;
import com.project2.domain.place.enums.Region;
import com.project2.domain.post.dto.PostResponseDTO;
import com.project2.domain.post.entity.Comment;
import com.project2.domain.post.entity.Likes;
import com.project2.domain.post.entity.Post;
import com.project2.domain.post.entity.PostImage;
import com.project2.domain.post.entity.Scrap;
import com.project2.domain.rank.dto.PopularPlaceDTO;
import com.project2.domain.rank.dto.RegionRankingDTO;
import com.project2.domain.rank.enums.RankingPeriod;
import com.project2.domain.rank.enums.RankingSort;
import com.project2.domain.rank.repository.RankingRepository;

@ExtendWith(MockitoExtension.class)
class RankingServiceTest {

	@Mock
	private RankingRepository rankingRepository;

	@InjectMocks
	private RankingService rankingService;

	private Pageable pageable;

	@BeforeEach
	void setUp() {
		pageable = PageRequest.of(0, 10);
	}

	@Test
	@DisplayName("인기 지역 랭킹 조회 - 좋아요 총합 기준 정렬")
	void getRegionRankings_shouldReturnRankedByLikesTotal() {
		// Given
		List<RegionRankingDTO> mockData = List.of(
			new RegionRankingDTO(Region.SEOUL, 200L, 50L, 300L),
			new RegionRankingDTO(Region.BUSAN, 180L, 40L, 250L)
		);
		Page<RegionRankingDTO> mockPage = new PageImpl<>(mockData, pageable, mockData.size());

		doReturn(mockPage).when(rankingRepository).findRegionRankings(any(LocalDateTime.class), eq(pageable));

		// When
		Page<RegionRankingDTO> result = rankingService.getRegionRankings(RankingPeriod.ONE_MONTH, pageable);

		// Then
		assertThat(result).isNotNull();
		assertThat(result.getContent().size()).isEqualTo(2);
		assertThat(result.getContent().getFirst().getRegion()).isEqualTo(Region.SEOUL);
		verify(rankingRepository).findRegionRankings(any(LocalDateTime.class), eq(pageable));
	}

	@Test
	@DisplayName("전국 인기 장소 조회 - 좋아요 기준 정렬")
	void getPopularPlaces_shouldReturnSortedPlacesByLikes() {
		// Given
		List<PopularPlaceDTO> mockPlaces = List.of(
			new PopularPlaceDTO(1L, "한강공원", Region.SEOUL, 200L, 50L, 30L),
			new PopularPlaceDTO(2L, "광안리", Region.BUSAN, 180L, 40L, 25L)
		);
		Page<PopularPlaceDTO> mockPage = new PageImpl<>(mockPlaces, pageable, mockPlaces.size());

		doReturn(mockPage).when(rankingRepository)
			.findPopularPlaces(any(LocalDateTime.class), isNull(), isNull(), eq("LIKES"), eq(pageable));

		// When
		Page<PopularPlaceDTO> result = rankingService.getPopularPlaces(RankingPeriod.ONE_MONTH, null, null,
			RankingSort.LIKES, pageable);

		// Then
		assertThat(result).isNotNull();
		assertThat(result.getContent().size()).isEqualTo(2);
		assertThat(result.getContent().getFirst().getPlaceName()).isEqualTo("한강공원");
		verify(rankingRepository).findPopularPlaces(any(LocalDateTime.class), isNull(), isNull(), eq("LIKES"),
			eq(pageable));
	}

	@Test
	@DisplayName("특정 지역 내 인기 장소 조회 - 서울, 좋아요 기준 정렬")
	void getPopularPlacesByRegion_shouldReturnSortedPlacesByLikes() {
		// Given
		List<PopularPlaceDTO> mockPlaces = List.of(
			new PopularPlaceDTO(1L, "남산공원", Region.SEOUL, 150L, 70L, 30L)
		);
		Page<PopularPlaceDTO> mockPage = new PageImpl<>(mockPlaces, pageable, mockPlaces.size());

		doReturn(mockPage).when(rankingRepository)
			.findPopularPlaces(any(LocalDateTime.class), eq(Region.SEOUL), isNull(), eq("LIKES"), eq(pageable));

		// When
		Page<PopularPlaceDTO> result = rankingService.getPopularPlaces(RankingPeriod.ONE_MONTH, Region.SEOUL, null,
			RankingSort.LIKES, pageable);

		// Then
		assertThat(result).isNotNull();
		assertThat(result.getContent().size()).isEqualTo(1);
		assertThat(result.getContent().getFirst().getPlaceName()).isEqualTo("남산공원");
		verify(rankingRepository).findPopularPlaces(any(LocalDateTime.class), eq(Region.SEOUL), isNull(), eq("LIKES"),
			eq(pageable));
	}

	@Test
	@DisplayName("특정 장소의 게시글 조회 - 좋아요 순 정렬")
	void getPostsByPlace_shouldReturnSortedPostsByLikes() {
		// Given
		Place mockPlace = Place.builder()
			.id(100L)
			.name("강남역")
			.region(Region.SEOUL)
			.category(Category.AT4)
			.build();

		Member mockMember = Member.builder()
			.id(1L)
			.nickname("테스트 유저")
			.profileImageUrl("test_profile.jpg")
			.build();

		List<Likes> emptyLikes = new ArrayList<>();
		List<Scrap> emptyScraps = new ArrayList<>();
		List<Comment> emptyComments = new ArrayList<>();
		List<PostImage> emptyImages = new ArrayList<>();

		List<Post> mockPosts = List.of(
			Post.builder().id(1L).title("제목1").content("내용1").place(mockPlace).member(mockMember)
				.likes(emptyLikes).scraps(emptyScraps).comments(emptyComments).images(emptyImages).build(),
			Post.builder().id(2L).title("제목2").content("내용2").place(mockPlace).member(mockMember)
				.likes(emptyLikes).scraps(emptyScraps).comments(emptyComments).images(emptyImages).build()
		);

		Page<Post> mockPage = new PageImpl<>(mockPosts, pageable, mockPosts.size());

		when(rankingRepository.findPostsByPlace(eq(1L), any(LocalDateTime.class), eq("LIKES"), eq(pageable)))
			.thenReturn(mockPage);

		// When
		Page<PostResponseDTO> result = rankingService.getPostsByPlace(1L, RankingPeriod.ONE_MONTH, RankingSort.LIKES,
			pageable);

		// Then
		assertThat(result).isNotNull();
		assertThat(result.getContent().size()).isEqualTo(2);
		assertThat(result.getContent().getFirst().getPlaceDTO().getPlaceName()).isEqualTo("강남역");
		assertThat(result.getContent().getFirst().getLikeCount()).isEqualTo(0);
		assertThat(result.getContent().getFirst().getScrapCount()).isEqualTo(0);
		assertThat(result.getContent().getFirst().getCommentCount()).isEqualTo(0);
		assertThat(result.getContent().getFirst().getAuthor().getNickname()).isEqualTo("테스트 유저");
		verify(rankingRepository).findPostsByPlace(eq(1L), any(LocalDateTime.class), eq("LIKES"), eq(pageable));
	}
}
