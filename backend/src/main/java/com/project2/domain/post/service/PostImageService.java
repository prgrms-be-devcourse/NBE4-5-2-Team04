package com.project2.domain.post.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.project2.domain.post.entity.Post;
import com.project2.domain.post.entity.PostImage;
import com.project2.domain.post.repository.PostImageRepository;
import com.project2.global.util.Ut;

@Service
public class PostImageService {
	private final PostImageRepository postImageRepository;

	private String uploadDir;
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
	 * @param deletedFileName 삭제된 파일명 리스트 없을 경우 빈 배열 주면 됨
	 * @return 저장된 이미지의 URL 리스트
	 * @throws IOException 파일 저장 중 오류 발생 시 예외 발생
	 */
	public List<String> saveImages(Post post, List<MultipartFile> images, List<String> deletedFileName) throws
		IOException {
		List<String> imageUrls = new ArrayList<>();
		deletedFileName.sort(Comparator.naturalOrder());

		// post-id별 디렉토리 생성
		File postDir = new File(uploadPostImageDir, String.valueOf(post.getId()));
		if (!postDir.exists()) {
			postDir.mkdirs();
		}

		for (int i = 0; i < images.size(); i++) {
			MultipartFile image = images.get(i);
			String originalFilename = image.getOriginalFilename();
			String extension = "";

			if (originalFilename != null && originalFilename.contains(".")) {
				extension = originalFilename.substring(originalFilename.lastIndexOf("."));
			}

			String fileName;
			int maxFileName = 0;
			if (post.getImages() != null) {
				maxFileName = post.getImages().size();
			}

			if (!deletedFileName.isEmpty()) {
				fileName = deletedFileName.remove(0) + extension;
			} else {
				fileName = (maxFileName + i) + extension;
			}

			// 파일 경로 설정
			Path filePath = Paths.get(uploadPostImageDir, String.valueOf(post.getId()), fileName);
			Files.copy(image.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

			// 🚀 수정된 부분: uploadDir을 포함하면서, 경로 구분자를 통일하여 URL 생성
			String imageUrl = Paths.get(uploadDir, "post-images", String.valueOf(post.getId()), fileName)
				.toString()
				.replace("\\", "/"); // 윈도우에서 `\` 대신 `/`로 변환

			imageUrl = "/" + imageUrl;

			imageUrls.add(imageUrl);

			PostImage postImage = new PostImage();
			postImage.setPost(post);
			postImage.setImageUrl(imageUrl);
			postImageRepository.save(postImage);
		}

		return imageUrls;
	}

	/**
	 * 주어진 게시글 ID(postId)에 대한 이미지를 업데이트
	 * 기존 이미지와 새 이미지를 비교하여 변경 사항을 반영
	 * - 기존 이미지와 동일한 해시값을 가진 이미지는 유지
	 * - 기존에 없던 새로운 이미지는 저장
	 * - 기존 이미지 중 새 이미지 목록에 없는 것은 삭제
	 *
	 * @param post 게시글 (이미지를 업데이트할 대상 게시글)
	 * @param newImages 새로운 업로드된 이미지 목록 (MultipartFile 리스트)
	 * @return 업데이트된 이미지의 URL 리스트
	 * @throws IOException 파일 저장 또는 삭제 중 오류 발생 시 예외 발생
	 * @throws NoSuchAlgorithmException 해시 계산 중 오류 발생 시 예외 발생
	 */
	public List<String> updateImages(Post post, List<MultipartFile> newImages) throws
		IOException,
		NoSuchAlgorithmException {
		Set<PostImage> existingImages = post.getImages();
		Map<String, PostImage> existingImageMap = new HashMap<>();

		// 기존 이미지 해시값 계산
		for (PostImage image : existingImages) {
			File file = new File(image.getImageUrl());
			if (file.exists()) {
				String hash = Ut.getFileChecksum(file);
				existingImageMap.put(hash, image);
			}
		}

		// 새로운 이미지의 해시값 리스트 생성 (중복 방지)
		Set<String> newImageHashes = new HashSet<>();
		for (MultipartFile newImage : newImages) {
			newImageHashes.add(Ut.getFileChecksum(newImage));
		}

		// 기존 이미지 중 새로운 이미지 목록에 없는 것은 삭제
		List<PostImage> imagesToDelete = existingImageMap.entrySet()
			.stream()
			.filter(entry -> !newImageHashes.contains(entry.getKey()))
			.map(Map.Entry::getValue)
			.toList();

		List<String> deletedFileNames = new ArrayList<>();

		// 기존 파일 및 DB 에서 삭제
		for (PostImage image : imagesToDelete) {
			File file = new File(image.getImageUrl());
			if (file.exists()) {
				deletedFileNames.add(Ut.getFileNameWithoutExtension(file));
				file.delete();
			}
		}
		post.getImages().removeAll(imagesToDelete);

		List<MultipartFile> filteredNewImages = new ArrayList<>();
		for (MultipartFile newImage : newImages) {
			if (!existingImageMap.containsKey(Ut.getFileChecksum(newImage))) {
				filteredNewImages.add(newImage);
			}
		}

		// 중복되지 않은 새로운 이미지만 저장
		return saveImages(post, filteredNewImages, deletedFileNames);
	}
}
