package com.project2.domain.post.dto;

import java.util.List;

import com.project2.domain.post.entity.Post;
import com.project2.domain.post.entity.PostImage;

import lombok.Getter;

@Getter
public class PostFormResponseDTO {

	private final String title;

	private final String content;

	private final Long placeId;

	private final Double latitude;

	private final Double longitude;

	private final String placeName;

	private final String category;

	private final String region;

	private final List<String> images;

	public PostFormResponseDTO(Post post) {
		this.title = post.getTitle();
		this.content = post.getContent();
		this.placeId = post.getPlace().getId();
		this.latitude = post.getPlace().getLatitude();
		this.longitude = post.getPlace().getLongitude();
		this.placeName = post.getPlace().getName();
		this.category = post.getPlace().getCategory().getKrCategory();
		this.region = post.getPlace().getRegion().getKrRegion();
		this.images = post.getImages().stream().map(PostImage::getImageUrl).sorted().toList();
	}
}
