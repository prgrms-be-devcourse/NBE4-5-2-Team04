package com.project2.global.security.OAuth;

import com.project2.global.util.HttpClientService;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Component
public class GoogleOAuthClient implements OAuthClient {
    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public String getAccessToken(String authCode) {
        String tokenUrl = "https://oauth2.googleapis.com/token";

        Map<String, String> params = Map.of(
                "client_id", "GOOGLE_CLIENT_ID",
                "client_secret", "GOOGLE_CLIENT_SECRET",
                "grant_type", "authorization_code",
                "code", authCode,
                "redirect_uri", "http://localhost:8080/oauth/callback/google"
        );

        Map response = restTemplate.postForObject(tokenUrl, params, Map.class);
        return (String) response.get("access_token");
    }

    @Override
    public String getProfileImage(String accessToken) {
        String apiURL = "https://www.googleapis.com/oauth2/v2/userinfo";
        String responseBody = HttpClientService.get(apiURL, accessToken);

        return HttpClientService.extractProfileImageUrl(responseBody, "picture");
    }
}
