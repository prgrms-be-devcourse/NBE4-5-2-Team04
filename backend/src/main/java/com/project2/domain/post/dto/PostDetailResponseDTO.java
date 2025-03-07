package com.project2.domain.post.dto;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import com.project2.domain.member.dto.AuthorDTO;
import com.project2.domain.place.dto.PlaceDTO;

import lombok.Getter;

@Getter
public class PostDetailResponseDTO {
	private final Long id;
	private final String title;
	private final String content;
	private final List<String> imageUrls;
	private final Integer likeCount;
	private final Integer scrapCount;
	private final Boolean isLiked;
	private final Boolean isScrapped;
	private final LocalDateTime createdDate;
	private final LocalDateTime modifiedDate;
	private final PlaceDTO placeDTO;
	private final AuthorDTO authorDTO;

	public PostDetailResponseDTO(
		Long id, String title, String content,
		Long memberId, String nickname, String profileImageUrl,
		String placeName, String category,
		int likeCount, int scrapCount,
		Boolean isLiked, Boolean isScrapped,
		String imageUrls,
		LocalDateTime createdDate, LocalDateTime modifiedDate
	) {
		this.id = id;
		this.title = title;
		this.content = content;
		this.likeCount = likeCount;
		this.scrapCount = scrapCount;
		this.imageUrls = Optional.ofNullable(imageUrls)
			.filter(str -> !str.isEmpty())
			.map(str -> Arrays.asList(str.split(",")))
			.orElse(Collections.emptyList());
		this.isLiked = isLiked != null ? isLiked : false;
		this.isScrapped = isScrapped != null ? isScrapped : false;
		this.createdDate = createdDate;
		this.modifiedDate = modifiedDate;
		this.authorDTO = new AuthorDTO(memberId, nickname, profileImageUrl);
		this.placeDTO = new PlaceDTO(placeName, category);
	}
}