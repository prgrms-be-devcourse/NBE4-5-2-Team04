package com.project2.domain.post.dto;

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
	private final List<String> imageUrl;

	public PostResponseDTO(Post post) {
		this.id = post.getId();
		this.title = post.getTitle();
		this.content = post.getContent();
		this.latitude = post.getLatitude();
		this.longitude = post.getLongitude();
		this.imageUrl = post.getImages().stream()
			.map(PostImage::getImageUrl)
			.collect(Collectors.toList());
	}
}