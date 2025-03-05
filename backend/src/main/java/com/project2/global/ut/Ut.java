package com.project2.global.ut;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.springframework.web.multipart.MultipartFile;

public class Ut {

	/**
	 * 파일의 SHA-256 해시값을 계산
	 */
	public static String getFileChecksum(File file) throws IOException, NoSuchAlgorithmException {
		MessageDigest digest = MessageDigest.getInstance("SHA-256");
		try (FileInputStream fis = new FileInputStream(file)) {
			byte[] byteArray = new byte[1024];
			int bytesRead;
			while ((bytesRead = fis.read(byteArray)) != -1) {
				digest.update(byteArray, 0, bytesRead);
			}
		}
		return bytesToHex(digest.digest());
	}

	/**
	 * MultipartFile 의 SHA-256 해시값을 계산
	 */
	public static String getFileChecksum(MultipartFile file) throws IOException, NoSuchAlgorithmException {
		MessageDigest digest = MessageDigest.getInstance("SHA-256");
		try (var inputStream = file.getInputStream()) {
			byte[] byteArray = new byte[1024];
			int bytesRead;
			while ((bytesRead = inputStream.read(byteArray)) != -1) {
				digest.update(byteArray, 0, bytesRead);
			}
		}
		return bytesToHex(digest.digest());
	}

	/**
	 * 바이트 배열을 16진수 문자열로 변환
	 */
	public static String bytesToHex(byte[] bytes) {
		StringBuilder sb = new StringBuilder();
		for (byte b : bytes) {
			sb.append(String.format("%02x", b));
		}
		return sb.toString();
	}

	public static String getFileNameWithoutExtension(File file) {
		String fileName = file.getName();
		int dotIndex = fileName.lastIndexOf(".");
		return (dotIndex == -1) ? fileName : fileName.substring(0, dotIndex);
	}
}
