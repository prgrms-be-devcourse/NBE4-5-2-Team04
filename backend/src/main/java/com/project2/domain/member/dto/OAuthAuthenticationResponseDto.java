package com.project2.domain.member.dto;

import com.project2.domain.member.entity.Member;
import com.project2.domain.member.enums.Provider;
import jakarta.validation.constraints.Email;
import lombok.NonNull;

public class OAuthAuthenticationResponseDto {
    @Email
    private String email;
    @NonNull
    private String nickname;
    @NonNull
    private Provider provider;

    public OAuthAuthenticationResponseDto(Member member) {
        this.email = member.getEmail();
        this.nickname = member.getNickname();
        this.provider = member.getProvider();
    }
}
