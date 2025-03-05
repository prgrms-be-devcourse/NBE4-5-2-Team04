package com.project2.domain.post.dto;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.project2.domain.post.entity.Post;
import com.project2.domain.post.entity.PostImage;

import lombok.Getter;

@Getter
public class PostResponseDTO {
	private final Long id;
	private final String title;
	private final String content;
	private final Double latitude;
	private final Double longitude;
	private final List<String> imageUrls;

	public PostResponseDTO(Post post, List<String> imageUrls) {
		this.id = post.getId();
		this.title = post.getTitle();
		this.content = post.getContent();
		this.latitude = post.getLatitude();
		this.longitude = post.getLongitude();
		this.imageUrls = imageUrls;
	}

	public PostResponseDTO(Post post) {
		this.id = post.getId();
		this.title = post.getTitle();
		this.content = post.getContent();
		this.latitude = post.getLatitude();
		this.longitude = post.getLongitude();
		this.imageUrls = (post.getImages() != null)
			? post.getImages().stream()
			.map(PostImage::getImageUrl)
			.collect(Collectors.toList())
			: Collections.emptyList();
	}
}