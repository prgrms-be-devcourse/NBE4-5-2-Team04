package com.project2.global.util;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.web.multipart.MultipartFile;

import javax.crypto.SecretKey;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.Map;

public class Ut {
	public static class Json {

		private static final ObjectMapper objectMapper = new ObjectMapper();

		public static String toString(Object obj) {
			try {
				return objectMapper.writeValueAsString(obj);
			} catch (JsonProcessingException e) {
				throw new RuntimeException(e);
			}
		}
	}

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

	public static class Jwt {
		public static String createToken(String keyString, int expireSeconds, Map<String, Object> claims) {

			SecretKey secretKey = Keys.hmacShaKeyFor(keyString.getBytes());

			Date issuedAt = new Date();
			Date expiration = new Date(issuedAt.getTime() + 1000L * expireSeconds);

			String jwt = Jwts.builder()
				.claims(claims)
				.issuedAt(issuedAt)
				.expiration(expiration)
				.signWith(secretKey)
				.compact();

			return jwt;
		}

		public static boolean isValidToken(String keyString, String token) {
			try {

				SecretKey secretKey = Keys.hmacShaKeyFor(keyString.getBytes());

				Jwts
					.parser()
					.verifyWith(secretKey)
					.build()
					.parse(token);

			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}

			return true;

		}

		public static Map<String, Object> getPayload(String keyString, String jwtStr) {

			SecretKey secretKey = Keys.hmacShaKeyFor(keyString.getBytes());

			return (Map<String, Object>)Jwts
				.parser()
				.verifyWith(secretKey)
				.build()
				.parse(jwtStr)
				.getPayload();

		}
	}
}
