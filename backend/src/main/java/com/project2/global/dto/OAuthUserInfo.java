package com.project2.global.dto;

import lombok.Getter;

@Getter
public class OAuthUserInfo {
    private String profileImageUrl;

    public OAuthUserInfo(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }
}
