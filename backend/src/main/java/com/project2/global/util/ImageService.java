package com.project2.global.util;

import com.project2.domain.member.enums.Provider;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.stereotype.Service;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

@Service
public class ImageService {

    public String downloadProfileImage(Provider provider, String imageUrl) {

        if(imageUrl == null || imageUrl.isEmpty()) {
            return "";
        }

        try {
            URL url = new URL(imageUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            try (InputStream inputStream = connection.getInputStream();
                 FileOutputStream outputStream = new FileOutputStream("/path/to/storage/" + provider.toString() + "_profile.jpg")) {

                IOUtils.copy(inputStream, outputStream);
            }

            return "/path/to/storage/" + provider + "_profile.jpg"; // 저장된 이미지 경로 반환
        } catch (Exception e) {
            throw new RuntimeException("이미지 다운로드 실패: " + e.getMessage(), e);
        }
    }
}