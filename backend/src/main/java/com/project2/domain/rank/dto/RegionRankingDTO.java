package com.project2.domain.rank.dto;

import org.springframework.lang.NonNull;

import com.project2.domain.place.enums.Region;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RegionRankingDTO {
	@NonNull
	private final Region region;

	@NonNull
	private final Long likeCount;

	@NonNull
	private final Long scrapCount;

	@NonNull
	private final Long postCount;
}