package com.project2.domain.post.dto;

import java.util.List;

import com.project2.domain.member.dto.AuthorDTO;
import com.project2.domain.place.dto.PlaceDTO;
import com.project2.domain.post.entity.Post;
import com.project2.domain.post.entity.PostImage;

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

	public PostResponseDTO(Post post) {
		this.id = post.getId();
		this.title = post.getTitle();
		this.content = post.getContent();
		this.placeDTO = new PlaceDTO(post.getPlace().getName(), post.getPlace().getCategory());
		this.likeCount = post.getLikes().size();
		this.scrapCount = post.getScraps().size();
		this.commentCount = post.getComments().size();
		this.imageUrls = post.getImages().stream().map(PostImage::getImageUrl).toList();
		this.author = new AuthorDTO(post.getMember().getId(), post.getMember().getNickname(), post.getMember().getProfileImageUrl());
	}
}
