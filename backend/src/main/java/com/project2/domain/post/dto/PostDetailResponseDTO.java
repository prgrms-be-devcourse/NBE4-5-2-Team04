package com.project2.domain.post.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.project2.domain.member.dto.AuthorDTO;
import com.project2.domain.member.entity.Member;
import com.project2.domain.place.dto.PlaceDTO;
import com.project2.domain.post.entity.Post;
import com.project2.domain.post.entity.PostImage;

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

	public PostDetailResponseDTO(Post post, Member actor) {
		this.id = post.getId();
		this.title = post.getTitle();
		this.content = post.getContent();
		this.likeCount = post.getLikes().size();
		this.scrapCount = post.getScraps().size();
		this.imageUrls = post.getImages().stream().map(PostImage::getImageUrl).toList();
		this.isLiked = post.getLikes().stream().anyMatch(like -> like.getMember().equals(actor));
		this.isScrapped = post.getScraps().stream().anyMatch(scrap -> scrap.getMember().equals(actor));
		this.createdDate = post.getCreatedDate();
		this.modifiedDate = post.getModifiedDate();
		this.authorDTO = new AuthorDTO(post.getMember().getId(), post.getMember().getNickname(),
			post.getMember().getProfileImageUrl());
		this.placeDTO = new PlaceDTO(post.getPlace().getName(), post.getPlace().getKrCategory());
	}
}