package com.project2.domain.member.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.project2.domain.member.entity.Member;

@Getter
@Setter
@AllArgsConstructor
public class FollowerResponseDto {
    private Long userId;
    private String email;
    private String nickname;
    private String profileImageUrl;

    public static FollowerResponseDto fromEntity(Member member) {
        return new FollowerResponseDto(
                member.getId(),
                member.getEmail(),
                member.getNickname(),
                member.getProfileImageUrl()
        );
    }
}
