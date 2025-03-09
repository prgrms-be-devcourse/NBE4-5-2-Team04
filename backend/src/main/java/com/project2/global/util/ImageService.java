package com.project2.global.util;

import org.springframework.stereotype.Service;

import java.io.File;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

@Service
public class ImageService {

    public String downloadProfileImage(String imageUrl, Long memberId) {

        if (imageUrl == null || imageUrl.isEmpty()) {
            return "";
        }

        String relativePath = "../frontend/public" + "/profiles/" + memberId + "/";
        String savePath = relativePath + "profile.png"; //

        try {
            URL url = new URL(imageUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            // User-Agent 설정 (네이버가 요청을 차단할 수 있기 때문)
            connection.setRequestProperty("User-Agent", "Mozilla/5.0");
            connection.setRequestProperty("Referer", "https://www.naver.com");

            try (InputStream inputStream = connection.getInputStream()) {
                File file = new File(savePath);
                file.getParentFile().mkdirs(); // 폴더가 없으면 생성
                Files.copy(inputStream, file.toPath(), StandardCopyOption.REPLACE_EXISTING);
            }
            return savePath.substring("../frontend/public".length());
        } catch (Exception e) {
            throw new RuntimeException("이미지 다운로드 실패: " + e.getMessage(), e);
        }
    }
}