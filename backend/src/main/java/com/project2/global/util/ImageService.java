package com.project2.global.util;

import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
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
        String savePath = relativePath + "profile.png";

        try {
            URL url = new URL(imageUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            connection.setRequestProperty("User-Agent", "Mozilla/5.0");
            connection.setRequestProperty("Referer", "https://www.naver.com");

            try (InputStream inputStream = connection.getInputStream();
                 FileOutputStream outputStream = new FileOutputStream(savePath)) {

                File file = new File(savePath);
                file.getParentFile().mkdirs();

                byte[] buffer = new byte[8192];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
            } finally {
                connection.disconnect();
            }

            return savePath.substring("../frontend/public".length());
        } catch (Exception e) {
            throw new RuntimeException("이미지 다운로드 실패: " + e.getMessage(), e);
        }
    }
}