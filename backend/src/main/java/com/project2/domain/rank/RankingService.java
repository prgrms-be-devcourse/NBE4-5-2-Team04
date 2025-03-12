package com.project2.domain.rank;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.project2.domain.place.enums.Region;
import com.project2.domain.post.dto.PostResponseDTO;
import com.project2.domain.rank.dto.PopularPlaceDTO;
import com.project2.domain.rank.dto.RegionRankingDTO;
import com.project2.domain.rank.enums.RankingPeriod;
import com.project2.domain.rank.enums.RankingSort;
import com.project2.domain.rank.repository.RankingRepository;
import com.project2.global.security.SecurityUser;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RankingService {

	private final RankingRepository rankingRepository;

	// 전국 및 특정 지역 인기 장소 조회 (좋아요 or 스크랩 기준) -> 전국 조회 시 region null 값
	public Page<PopularPlaceDTO> getPopularPlaces(RankingPeriod period, Region region, String placeName,
		RankingSort sort, Pageable pageable) {
		LocalDateTime startDate = period.getStartDate();
		String sortParam = (sort != null) ? sort.name() : RankingSort.LIKES.name();
		return rankingRepository.findPopularPlaces(startDate, region, placeName, sortParam, pageable);
	}

	// 인기 지역 랭킹 조회 (좋아요 합 기준)
	public Page<RegionRankingDTO> getRegionRankings(RankingPeriod period, Pageable pageable) {
		LocalDateTime startDate = period.getStartDate();
		return rankingRepository.findRegionRankings(startDate, pageable);
	}

	// 특정 지역의 게시글 조회
	public Page<PostResponseDTO> getPostsByRegion(Region region, RankingPeriod period, Pageable pageable,
		SecurityUser actor) {
		LocalDateTime startDate = period.getStartDate();
		return rankingRepository.findPostsByRegion(region, startDate, pageable)
			.map(post -> new PostResponseDTO(post, actor));
	}

	// 특정 장소의 게시글 조회 (좋아요, 스크랩, 최신순)
	public Page<PostResponseDTO> getPostsByPlace(Long placeId, RankingPeriod period, RankingSort sort,
		Pageable pageable, SecurityUser actor) {
		LocalDateTime startDate = period.getStartDate();
		String sortParam = (sort != null) ? sort.name() : RankingSort.LIKES.name();
		return rankingRepository.findPostsByPlace(placeId, startDate, sortParam, pageable)
			.map(post -> new PostResponseDTO(post, actor));
	}
}