package com.project2.global.security.OAuth;

import com.project2.global.util.HttpClientService;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Component
public class NaverOAuthClient implements OAuthClient {
    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public String getAccessToken(String authCode) {
        String tokenUrl = "https://nid.naver.com/oauth2.0/token";

        Map<String, String> params = Map.of(
                "client_id", "naver_client_id",
                "client_secret", "naver_client_secret",
                "grant_type", "authorization_code",
                "code", authCode,
                "state", "random_state"
        );

        Map response = restTemplate.postForObject(tokenUrl, params, Map.class);
        return (String) response.get("access_token");
    }

    @Override
    public String getProfileImage(String accessToken) {
        String apiURL = "https://openapi.naver.com/v1/nid/me";
        String responseBody = HttpClientService.get(apiURL, accessToken);

        return HttpClientService.extractProfileImageUrl(responseBody, "profile_image");
    }
}
