package com.project2.domain.member.dto;

import com.project2.domain.member.entity.Member;
import lombok.Getter;
import org.springframework.lang.NonNull;

@Getter
public class MemberDTO {
    @NonNull
    private long id;
    @NonNull
    private String nickname;
    @NonNull
    private String profileImageUrl;

    public MemberDTO(Member member) {
        this.id = member.getId();
        this.nickname = member.getNickname();
        this.profileImageUrl = member.getProfileImageUrlOrDefaultUrl();
    }
}