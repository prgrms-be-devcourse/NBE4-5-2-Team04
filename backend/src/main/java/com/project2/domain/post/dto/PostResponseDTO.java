package com.project2.domain.post.dto;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import com.project2.domain.member.dto.AuthorDTO;
import com.project2.domain.place.dto.PlaceDTO;

import lombok.Getter;

@Getter
public class PostResponseDTO {
	private final Long id;
	private final String title;
	private final String content;
	private final PlaceDTO placeDTO;
	private final Integer likeCount;
	private final Integer scrapCount;
	private final Integer commentCount;
	private final List<String> imageUrls;
	private final AuthorDTO author;

	public PostResponseDTO(Long id, String title, String content,
		String placeName, String placeCategory,
		int likeCount, int scrapCount, int commentCount,
		String imageUrls,
		Long memberId, String nickname, String profileImageUrl) {
		this.id = id;
		this.title = title;
		this.content = content;
		this.placeDTO = new PlaceDTO(placeName, placeCategory);
		this.likeCount = likeCount;
		this.scrapCount = scrapCount;
		this.commentCount = commentCount;
		this.imageUrls = Optional.ofNullable(imageUrls)
			.filter(str -> !str.isEmpty())
			.map(str -> Arrays.asList(str.split(",")))
			.orElse(Collections.emptyList());
		this.author = new AuthorDTO(memberId, nickname, profileImageUrl);
	}
}
