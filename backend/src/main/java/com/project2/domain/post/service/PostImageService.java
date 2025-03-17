package com.project2.domain.post.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.project2.domain.post.entity.Post;
import com.project2.domain.post.entity.PostImage;
import com.project2.domain.post.repository.PostImageRepository;

@Service
public class PostImageService {
	private final PostImageRepository postImageRepository;

	private final String uploadDir;
	private final String uploadPostImageDir;

	public PostImageService(PostImageRepository postImageRepository
		, @Value("${custom.file.upload-dir}") String uploadDir) {
		this.postImageRepository = postImageRepository;
		this.uploadDir = uploadDir;
		this.uploadPostImageDir = uploadDir + "post-images";
	}

	/**
	 * 주어진 게시글 ID(postId) 디렉토리에 업로드된 이미지들을 저장하고, 저장된 이미지 URL 리스트를 반환
	 * 저장 방식: {postId}/{업로드 순번}.{확장자} 형식으로 저장
	 * 예시: 123/0.jpg, 123/1.png
	 *
	 * @param post 게시글 (이미지를 저장할 디렉토리명)
	 * @param images 업로드된 이미지 목록
	 * @throws IOException 파일 저장 중 오류 발생 시 예외 발생
	 */
	public void saveImages(Post post, List<MultipartFile> images) throws
		IOException {

		// post-id별 디렉토리 생성
		File postDir = new File(uploadPostImageDir, String.valueOf(post.getId()));
		if (!postDir.exists()) {
			postDir.mkdirs();
		}

		for (int i = 0; i < images.size(); i++) {
			MultipartFile image = images.get(i);
			String originalFilename = image.getOriginalFilename();
			String extension = "";

			if (originalFilename.contains(".")) {
				extension = originalFilename.substring(originalFilename.lastIndexOf("."));
			}

			// 파일 경로 설정
			Path filePath = Paths.get(uploadPostImageDir, String.valueOf(post.getId()), i + extension);
			Files.copy(image.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

			// 🚀 수정된 부분: uploadDir 을 포함하면서, 경로 구분자를 통일하여 URL 생성
			String imageUrl = Paths.get(uploadDir, "post-images", String.valueOf(post.getId()), i + extension)
				.toString()
				.replace("\\", "/"); // 윈도우에서 `\` 대신 `/`로 변환

			imageUrl = "/" + imageUrl;

			PostImage postImage = new PostImage();
			postImage.setPost(post);
			postImage.setImageUrl(imageUrl);
			postImageRepository.save(postImage);
		}
	}

	/**
	 * 주어진 게시글 ID(postId)에 대한 이미지를 업데이트
	 * 기존에 postId 에 해당 directory 비우고 db 도 전부 삭제
	 *
	 * @param post 게시글 (이미지를 업데이트할 대상 게시글)
	 * @param newImages 새로운 업로드된 이미지 목록 (MultipartFile 리스트)
	 * @throws IOException 파일 저장 또는 삭제 중 오류 발생 시 예외 발생
	 */
	public void updateImages(Post post, List<MultipartFile> newImages) throws
		IOException {

		// db 전부 삭제
		post.getImages().clear();

		// 폴더 안 전부 삭제
		File folder = new File(uploadPostImageDir + "/" + post.getId());
		if (folder.exists() && folder.isDirectory()) {
			File[] files = folder.listFiles();
			if (files != null) {
				for (File file : files) {
					file.delete();
				}
			}
		}
		if (newImages != null) {
			saveImages(post, newImages);
		}
	}
}
