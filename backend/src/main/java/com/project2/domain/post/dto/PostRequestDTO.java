package com.project2.domain.post.dto;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.project2.domain.post.validation.ImageFile;

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

	@NotBlank(message = "제목을 입력해주세요.")
	private String title;

	@NotBlank(message = "내용을 입력해주세요.")
	private String content;

	@NotNull(message = "장소 ID는 필수입니다.")
	private Long placeId;

	@NotNull(message = "위도(latitude)는 필수입니다.")
	private Double latitude;

	@NotNull(message = "경도(longitude)는 필수입니다.")
	private Double longitude;

	@NotBlank(message = "장소 이름은 필수입니다.")
	private String placeName;

	@NotBlank(message = "카테고리는 필수입니다.")
	private String category;

	@NotBlank(message = "지역(Region)은 필수입니다.")
	private String region;

	@NotNull(message = "회원 ID는 필수입니다.")
	private Long memberId;
	@ImageFile
	private List<MultipartFile> images;

}
