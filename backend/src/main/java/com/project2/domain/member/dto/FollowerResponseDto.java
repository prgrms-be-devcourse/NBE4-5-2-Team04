package com.project2.domain.member.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.project2.domain.member.entity.Member;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FollowerResponseDto {
    private Long userId;
    private String email;
    private String nickname;
    private String profileImageUrl;
    private int totalPages; // totalPages 추가


    public static FollowerResponseDto fromEntity(Member member) {
        return new FollowerResponseDto(
                member.getId(),
                member.getEmail(),
                member.getNickname(),
                member.getProfileImageUrl(),
                0 // 초기값 설정 (나중에 컨트롤러에서 설정)
        );
    }
}
