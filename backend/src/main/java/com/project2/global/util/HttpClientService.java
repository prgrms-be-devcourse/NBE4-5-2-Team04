package com.project2.global.util;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

public class HttpClientService {

    public static String get(String apiUrl, String accessToken) {
        HttpURLConnection con = connect(apiUrl);
        try {
            con.setRequestMethod("GET");
            con.setRequestProperty("Authorization", "Bearer " + accessToken);

            int responseCode = con.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) { // 정상 호출
                return readBody(con.getInputStream());
            } else { // 에러 발생
                return readBody(con.getErrorStream());
            }
        } catch (IOException e) {
            throw new RuntimeException("API 요청과 응답 실패", e);
        } finally {
            con.disconnect();
        }
    }

    private static HttpURLConnection connect(String apiUrl) {
        try {
            URL url = new URL(apiUrl);
            return (HttpURLConnection) url.openConnection();
        } catch (IOException e) {
            throw new RuntimeException("연결 실패: " + apiUrl, e);
        }
    }

    private static String readBody(InputStream body) {
        try (BufferedReader lineReader = new BufferedReader(new InputStreamReader(body))) {
            StringBuilder responseBody = new StringBuilder();
            String line;
            while ((line = lineReader.readLine()) != null) {
                responseBody.append(line);
            }
            return responseBody.toString();
        } catch (IOException e) {
            throw new RuntimeException("API 응답 읽기 실패", e);
        }
    }

    public static String extractProfileImageUrl(String jsonResponse, String key) {
        try {
            Map<String, Object> responseMap = new ObjectMapper().readValue(jsonResponse, Map.class);
            if (responseMap.containsKey("response")) {
                Map<String, Object> userInfo = (Map<String, Object>) responseMap.get("response");
                return (String) userInfo.get(key);
            }
            return null;
        } catch (Exception e) {
            throw new RuntimeException("JSON 파싱 실패: " + jsonResponse, e);
        }
    }
}
