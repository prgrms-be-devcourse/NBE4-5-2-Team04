package com.project2.domain.rank.dto;

import org.springframework.lang.NonNull;

import com.project2.domain.place.enums.Region;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PopularPlaceDTO {
	@NonNull
	private final Long placeId;

	@NonNull
	private final String placeName;

	@NonNull
	private final String region;

	@NonNull
	private final Long likeCount;

	@NonNull
	private final Long scrapCount;

	@NonNull
	private final Long postCount;

	public PopularPlaceDTO(Long placeId, String placeName, Region region, Long likeCount, Long scrapCount,
		Long postCount) {
		this.placeId = placeId;
		this.placeName = placeName;
		this.region = region.getKrRegion();
		this.likeCount = likeCount;
		this.scrapCount = scrapCount;
		this.postCount = postCount;
	}
}