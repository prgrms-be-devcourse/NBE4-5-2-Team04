package com.project2.global.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ImageService {

	private static final String PROFILE_IMAGE_DIR = "../frontend/public" + "/profiles/";

	/**
	 * URL을 통해 이미지를 다운로드하여 저장하는 메서드
	 */
	public String downloadProfileImage(String imageUrl, Long memberId) {

		if (imageUrl == null || imageUrl.isEmpty()) {
			return "";
		}

		String relativePath = PROFILE_IMAGE_DIR + memberId + "/";
		String savePath = relativePath + "profile.png";

		try {
			URL url = new URL(imageUrl);
			HttpURLConnection connection = (HttpURLConnection)url.openConnection();
			connection.setRequestMethod("GET");

			connection.setRequestProperty("User-Agent", "Mozilla/5.0");
			connection.setRequestProperty("Referer", "https://www.naver.com");

			try (InputStream inputStream = connection.getInputStream()) {

				File file = new File(savePath);
				file.getParentFile().mkdirs();
				Files.copy(inputStream, file.toPath(), StandardCopyOption.REPLACE_EXISTING);

			} finally {
				connection.disconnect();
			}

			return savePath.substring("../frontend/public".length());
		} catch (Exception e) {
			throw new RuntimeException("이미지 다운로드 실패: " + e.getMessage(), e);
		}
	}

	/**
	 * 사용자가 직접 업로드한 이미지를 저장하는 메서드
	 */
	public String storeProfileImage(Long memberId, MultipartFile file) {
		try {
			String folderPath = PROFILE_IMAGE_DIR + memberId;
			File folder = new File(folderPath);
			if (!folder.exists()) {
				folder.mkdirs();
			}

			String filePath = folderPath + "/profile.png";
			Path path = Path.of(filePath);
			Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

			return "/profiles/" + memberId + "/profile.png";
		} catch (IOException e) {
			throw new RuntimeException("파일 저장 실패: " + e.getMessage(), e);
		}
	}
}