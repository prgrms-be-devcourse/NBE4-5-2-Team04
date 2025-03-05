package com.project2.global.security.OAuth;

public interface OAuthClient {
    String getAccessToken(String authCode);
    String getProfileImage(String accessToken);
}
