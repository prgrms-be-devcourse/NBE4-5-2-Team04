package com.project2.global.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ImageService {

	@Value("${custom.file.upload-dir}")
	private String uploadDir;

	private static final String PROFILE_DIR = "profiles/";

	/**
	 * URL을 통해 이미지를 다운로드하여 저장하는 메서드
	 */
	public String downloadProfileImage(String imageUrl, Long memberId) {
		if (imageUrl == null || imageUrl.isEmpty()) {
			return "";
		}

		String folderPath = uploadDir + PROFILE_DIR + memberId + "/";
		long currentTimestamp = System.currentTimeMillis();
		String savePath = folderPath + currentTimestamp + ".png";

		try {
			URL url = new URL(imageUrl);
			HttpURLConnection connection = (HttpURLConnection)url.openConnection();
			connection.setRequestMethod("GET");

			connection.setRequestProperty("User-Agent", "Mozilla/5.0");
			connection.setRequestProperty("Referer", "https://www.naver.com");

			try (InputStream inputStream = connection.getInputStream()) {
				deleteExistingFiles(folderPath);

				File file = new File(savePath);
				file.getParentFile().mkdirs();
				Files.copy(inputStream, file.toPath(), StandardCopyOption.REPLACE_EXISTING);
			} finally {
				connection.disconnect();
			}

			return "/" + savePath;
		} catch (Exception e) {
			throw new RuntimeException("이미지 다운로드 실패: " + e.getMessage(), e);
		}
	}

	/**
	 * 사용자가 직접 업로드한 이미지를 저장하는 메서드
	 */
	public String storeProfileImage(Long memberId, MultipartFile file) {
		try {
			long currentTimestamp = System.currentTimeMillis();
			String folderPath = uploadDir + PROFILE_DIR + memberId;
			File folder = new File(folderPath);
			if (!folder.exists()) {
				folder.mkdirs();
			}

			deleteExistingFiles(folderPath);

			String fileExtension = getFileExtension(file.getOriginalFilename());
			String newFileName = currentTimestamp + "." + fileExtension;
			String filePath = folderPath + "/" + newFileName;

			Path path = Path.of(filePath);
			Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

			return "/" + filePath;
		} catch (IOException e) {
			throw new RuntimeException("파일 저장 실패: " + e.getMessage(), e);
		}
	}

	/**
	 * 사용자의 프로필 폴더 내 모든 파일 삭제
	 */
	private void deleteExistingFiles(String folderPath) {
		File folder = new File(folderPath);
		if (folder.exists() && folder.isDirectory()) {
			File[] files = folder.listFiles();
			if (files != null) {
				for (File file : files) {
					if (!file.delete()) {
						throw new RuntimeException("기존 파일 삭제 실패: " + file.getAbsolutePath());
					}
				}
			}
		}
	}

	/**
	 * 파일 확장자 추출
	 */
	private String getFileExtension(String fileName) {
		if (fileName == null || fileName.isEmpty()) {
			return "png"; // 기본 확장자 설정
		}
		int lastDotIndex = fileName.lastIndexOf(".");
		return (lastDotIndex == -1) ? "png" : fileName.substring(lastDotIndex + 1);
	}
}