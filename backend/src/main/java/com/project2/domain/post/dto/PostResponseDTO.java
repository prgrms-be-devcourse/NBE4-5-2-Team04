package com.project2.domain.post.dto;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.project2.domain.post.entity.Post;

import lombok.Getter;

@Getter
public class PostResponseDTO {
	private final Long id;
	private final String title;
	private final String content;
	private final Double latitude;
	private final Double longitude;
	private final Integer likeCount;
	private final Integer scrapCount;
	private final List<String> imageUrls;
	private final LocalDateTime createdDate;
	private final LocalDateTime modifiedDate;

	// query 용
	public PostResponseDTO(Long id, String title, String content,
		Double latitude, Double longitude,
		Long likeCount, Long scrapCount,
		String imageUrls, LocalDateTime createdDate, LocalDateTime modifiedDate) {
		this.id = id;
		this.title = title;
		this.content = content;
		this.latitude = latitude;
		this.longitude = longitude;
		this.likeCount = likeCount != null ? likeCount.intValue() : 0;
		this.scrapCount = scrapCount != null ? scrapCount.intValue() : 0;
		this.imageUrls = (imageUrls != null && !imageUrls.isEmpty())
			? Arrays.asList(imageUrls.split(","))
			: Collections.emptyList();
		this.createdDate = createdDate;
		this.modifiedDate = modifiedDate;
	}

	// create 용
	public PostResponseDTO(Post post, List<String> imageUrls) {
		this.id = post.getId();
		this.title = post.getTitle();
		this.content = post.getContent();
		this.latitude = post.getLatitude();
		this.longitude = post.getLongitude();
		this.likeCount = 0;
		this.scrapCount = 0;
		this.imageUrls = imageUrls;
		this.createdDate = post.getCreatedDate();
		this.modifiedDate = post.getModifiedDate();
	}
}