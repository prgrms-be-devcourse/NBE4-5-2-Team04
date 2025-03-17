package com.project2.domain.rank;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project2.domain.place.enums.Region;
import com.project2.domain.post.dto.PostResponseDTO;
import com.project2.domain.rank.dto.PopularPlaceDTO;
import com.project2.domain.rank.dto.RegionRankingDTO;
import com.project2.domain.rank.enums.RankingPeriod;
import com.project2.domain.rank.repository.RankingRepository;
import com.project2.global.security.SecurityUser;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RankingService {

	private final RankingRepository rankingRepository;

	// 전국 인기 장소 조회
	@Transactional
	public Page<PopularPlaceDTO> getPopularPlaces(RankingPeriod period, String placeName, Pageable pageable) {
		LocalDateTime startDate = period.getStartDate();
		return rankingRepository.findPopularPlaces(startDate, placeName, pageable);
	}

	// 인기 지역 랭킹 조회
	@Transactional
	public Page<RegionRankingDTO> getRegionRankings(RankingPeriod period, Pageable pageable) {
		LocalDateTime startDate = period.getStartDate();
		return rankingRepository.findRegionRankings(startDate, pageable);
	}

	// 특정 지역의 게시글 목록 조회
	@Transactional
	public Page<PostResponseDTO> getPostsByRegion(Region region, RankingPeriod period, Pageable pageable,
		SecurityUser actor) {
		LocalDateTime startDate = period.getStartDate();
		return rankingRepository.findPostsByRegion(region, startDate, pageable)
			.map(post -> new PostResponseDTO(post, actor));
	}

	// 특정 장소의 게시글 목록 조회
	@Transactional
	public Page<PostResponseDTO> getPostsByPlace(Long placeId, RankingPeriod period,
		Pageable pageable, SecurityUser actor) {

		LocalDateTime startDate = period.getStartDate();
		return rankingRepository.findPostsByPlace(placeId, startDate, pageable)
			.map(post -> new PostResponseDTO(post, actor));
	}
}