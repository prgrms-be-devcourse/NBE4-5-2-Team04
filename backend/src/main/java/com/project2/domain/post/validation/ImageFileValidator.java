package com.project2.domain.post.validation;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ImageFileValidator implements ConstraintValidator<ImageFile, List<MultipartFile>> {
	private static final List<String> ALLOWED_MIME_TYPES = List.of("image/jpeg", "image/png", "image/gif");

	@Override
	public boolean isValid(List<MultipartFile> files, ConstraintValidatorContext context) {
		if (files == null || files.isEmpty()) {
			return true; // 이미지 업로드가 필수 사항이 아니라면 허용
		}

		return files.stream()
			.allMatch(file -> ALLOWED_MIME_TYPES.contains(file.getContentType()));
	}
}