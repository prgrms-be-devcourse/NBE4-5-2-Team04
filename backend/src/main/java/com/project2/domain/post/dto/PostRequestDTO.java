package com.project2.domain.post.dto;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PostRequestDTO {

	@NotBlank
	private String title;
	@NotBlank
	private String content;
	@NotNull
	private Double latitude;
	@NotNull
	private Double longitude;
	@NotNull
	private Long placeId;
	@NotNull
	private Long memberId;
	private List<MultipartFile> images;

}
