package com.project2.global.security.OAuth;

import com.project2.domain.member.enums.Provider;
import com.project2.global.dto.OAuthUserInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@RequiredArgsConstructor
@Service
public class OAuthService {

    private final Map<Provider, OAuthClient> clients;

    @Autowired
    public OAuthService(NaverOAuthClient naverClient, GoogleOAuthClient googleClient) {
        this.clients = Map.of(
                Provider.NAVER, naverClient,
                Provider.GOOGLE, googleClient
        );
    }

    public OAuthUserInfo getOAuthUserInfo(Provider provider, String authCode) {
        OAuthClient client = clients.get(provider);
        if (client == null) {
            throw new IllegalArgumentException("지원되지 않는 OAuth provider: " + provider);
        }

        String accessToken = client.getAccessToken(authCode);
        String profileImageUrl = client.getProfileImage(accessToken);

        return new OAuthUserInfo(profileImageUrl);
    }
}
