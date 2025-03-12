package com.project2.domain.rank;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.project2.domain.place.enums.Region;
import com.project2.domain.post.dto.PostResponseDTO;
import com.project2.domain.rank.dto.PopularPlaceDTO;
import com.project2.domain.rank.dto.RegionRankingDTO;
import com.project2.domain.rank.enums.RankingPeriod;
import com.project2.domain.rank.enums.RankingSort;
import com.project2.global.dto.RsData;
import com.project2.global.security.SecurityUser;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/rankings")
@RequiredArgsConstructor
public class RankingController {

	private final RankingService rankingService;

	// 전국 및 특정 지역 인기 장소 목록 조회
	@GetMapping("/places")
	public RsData<Page<PopularPlaceDTO>> getPopularPlaces(
		@RequestParam(defaultValue = "ONE_MONTH") RankingPeriod period,
		@RequestParam(required = false) Region region,
		@RequestParam(required = false) String placeName,
		@RequestParam(defaultValue = "LIKES") RankingSort sort,
		@PageableDefault Pageable pageable) {

		return new RsData<>("200", "인기 장소 조회 성공",
			rankingService.getPopularPlaces(period, region, placeName, sort, pageable));
	}

	// 인기 지역 랭킹 조회 (지역별 좋아요 총합)
	@GetMapping("/regions")
	public RsData<Page<RegionRankingDTO>> getRegionRankings(
		@RequestParam(defaultValue = "ONE_MONTH") RankingPeriod period,
		@PageableDefault Pageable pageable) {

		return new RsData<>("200", "인기 지역 랭킹 조회 성공",
			rankingService.getRegionRankings(period, pageable));
	}

	// 특정 장소의 게시글 목록 조회 (좋아요 순, 스크랩 순, 최신순)
	@GetMapping("/regions/{region}/posts")
	public RsData<Page<PostResponseDTO>> getPostsByRegion(
		@PathVariable String region,
		@RequestParam(defaultValue = "ONE_MONTH") RankingPeriod period,
		@PageableDefault Pageable pageable,
		@AuthenticationPrincipal SecurityUser actor) {  // 현재 로그인한 사용자 정보

		Region regionEnum = Region.fromKrRegion(region);
		return new RsData<>("200", region + "의 게시글 조회 성공",
			rankingService.getPostsByRegion(regionEnum, period, pageable, actor));
	}

	// 특정 지역의 게시글 목록 조회
	@GetMapping("/places/{placeId}/posts")
	public RsData<Page<PostResponseDTO>> getPostsByPlace(
		@PathVariable Long placeId,
		@RequestParam(defaultValue = "ONE_MONTH") RankingPeriod period,
		@RequestParam(defaultValue = "LIKES") RankingSort sort,
		@PageableDefault Pageable pageable,
		@AuthenticationPrincipal SecurityUser actor) {  // 현재 로그인한 사용자 정보

		return new RsData<>("200", "장소의 게시글 조회 성공",
			rankingService.getPostsByPlace(placeId, period, sort, pageable, actor));
	}
}