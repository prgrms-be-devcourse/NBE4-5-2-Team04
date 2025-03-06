package com.project2.global.security;

import com.project2.domain.member.entity.Member;
import com.project2.domain.member.enums.Provider;
import com.project2.domain.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomOauth2UserService extends DefaultOAuth2UserService {

    private final MemberService memberService;

    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        String providerType = userRequest.getClientRegistration().getRegistrationId();
        Provider provider = "naver".equals(providerType) ? Provider.NAVER : Provider.GOOGLE;

        Map<String, Object> attributes = oAuth2User.getAttributes();

        String nickname = "";
        String profileImage = "";
        String email = "";

        if(provider == Provider.NAVER) {
            Map<String, Object> response = (Map<String, Object>)attributes.get("response");
            nickname = (String)response.get("nickname");
            profileImage = (String)response.get("profile_image");
            email = (String)response.get("email");
        } else {
            nickname = (String)attributes.get("name");
            profileImage = (String)attributes.get("picture");
            email = (String)attributes.get("email");
        }

        Optional<Member> opMember = memberService.findByEmail(email);

        if(opMember.isPresent()) {
            Member member = opMember.get();
            member.setNickname(nickname);

            return new SecurityUser(member);
        }

        Member member = memberService.signUp(email, nickname, profileImage, provider);

        return new SecurityUser(member);
    }
}