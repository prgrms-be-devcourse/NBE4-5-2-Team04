package com.project2.domain.rank;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.project2.domain.post.dto.PostResponseDTO;
import com.project2.domain.rank.dto.PopularPlaceDTO;
import com.project2.domain.rank.dto.RegionRankingDTO;
import com.project2.domain.rank.enums.RankingPeriod;
import com.project2.domain.rank.enums.RankingSort;
import com.project2.domain.rank.repository.RankingRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RankingService {

	private final RankingRepository rankingRepository;

	// 전국 인기 장소 조회
	public Page<PopularPlaceDTO> getPopularPlaces(
		RankingPeriod period, String placeName, RankingSort sort, Pageable pageable) {

		LocalDateTime startDate = period.getStartDate();
		String sortParam = (sort != null) ? sort.name() : RankingSort.LIKES.name();
		return rankingRepository.findPopularPlaces(startDate, null, placeName, sortParam, pageable);
	}

	// 특정 지역 내 인기 장소 조회
	public Page<PopularPlaceDTO> getPopularPlacesByRegion(
		RankingPeriod period, String region, String placeName, RankingSort sort, Pageable pageable) {

		LocalDateTime startDate = period.getStartDate();
		String sortParam = (sort != null) ? sort.name() : RankingSort.LIKES.name();
		return rankingRepository.findPopularPlacesByRegion(startDate, region, placeName, sortParam, pageable);
	}

	// 인기 지역 랭킹 조회
	public Page<RegionRankingDTO> getRegionRankings(RankingPeriod period, Pageable pageable) {
		LocalDateTime startDate = period.getStartDate();
		return rankingRepository.findRegionRankings(startDate, pageable);
	}

	// 특정 지역의 게시글 조회
	public Page<PostResponseDTO> getPostsByRegion(String region, RankingPeriod period, Pageable pageable) {
		LocalDateTime startDate = period.getStartDate();
		return rankingRepository.findPostsByRegion(region, startDate, pageable)
			.map(PostResponseDTO::new);
	}

	// 특정 장소의 게시글 조회 (정렬 옵션: 좋아요, 스크랩, 최신순)
	public Page<PostResponseDTO> getPostsByPlace(Long placeId, RankingPeriod period, RankingSort sort,
		Pageable pageable) {
		LocalDateTime startDate = period.getStartDate();
		String sortParam = (sort != null) ? sort.name() : RankingSort.LIKES.name();
		return rankingRepository.findPostsByPlace(placeId, startDate, sortParam, pageable)
			.map(PostResponseDTO::new);
	}
}