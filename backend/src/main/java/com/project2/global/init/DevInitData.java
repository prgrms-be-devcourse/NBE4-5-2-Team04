package com.project2.global.init;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Profile("dev")
@Configuration
public class DevInitData {

	@Value("${custom.url.base-url}")
	private String siteBaseUrl;

	@Bean
	public ApplicationRunner devApplicationRunner() {
		return args -> {
			genApiJsonFile(siteBaseUrl + "/v3/api-docs", "apiV1.json");
			runCmd(getPlatformSpecificCommand());
		};
	}

	private boolean isWindows() {
		return System.getProperty("os.name").toLowerCase().contains("win");
	}

	private List<String> getPlatformSpecificCommand() {
		String command = "npx --package typescript --package openapi-typescript --package punycode openapi-typescript apiV1.json -o ../frontend/src/lib/backend/schema.d.ts";
		if (isWindows()) {
			return List.of("cmd.exe", "/c", command);
		} else {
			return List.of("sh", "-c", command);
		}
	}

	public void runCmd(List<String> command) {
		try {
			ProcessBuilder processBuilder = new ProcessBuilder(command);
			processBuilder.redirectErrorStream(true); // 표준 에러를 표준 출력과 합침

			Process process = processBuilder.start();

			// 명령어 실행 결과 읽기
			try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
				String line;
				while ((line = reader.readLine()) != null) {
					System.out.println(line);
				}
			}

			// 프로세스 종료 코드 확인
			int exitCode = process.waitFor();
			System.out.println("프로세스 종료 코드: " + exitCode);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void genApiJsonFile(String url, String filename) {
		Path filePath = Path.of(filename); // 저장할 파일명
		HttpClient client = HttpClient.newHttpClient();
		HttpRequest request = HttpRequest.newBuilder()
			.uri(URI.create(url))
			.GET()
			.build();

		try {
			HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

			if (response.statusCode() == 200) {
				Files.writeString(filePath, response.body(), StandardOpenOption.CREATE,
					StandardOpenOption.TRUNCATE_EXISTING);
				System.out.println("JSON 데이터가 " + filePath.toAbsolutePath() + "에 저장되었습니다.");
			} else {
				System.err.println("오류: HTTP 상태 코드 " + response.statusCode());
			}
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
	}
}