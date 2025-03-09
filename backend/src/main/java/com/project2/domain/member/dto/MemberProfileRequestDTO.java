package com.project2.domain.member.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.project2.domain.member.entity.Member;
import lombok.Getter;
import lombok.NonNull;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Getter
public class MemberProfileRequestDTO {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy년 MM월");

    private String nickname;
    @JsonProperty(required = true)
    @NonNull
    private String profileImageUrl;
    private long totalPostCount;
    private long totalFlowerCount;
    private long totalFlowingCount;
    private String createdMonthYear;

    public MemberProfileRequestDTO(Member member, long totalPostCount, long totalFlowerCount, long totalFlowingCount) {
        this.nickname = member.getNickname();
        this.profileImageUrl = member.getProfileImageUrlOrDefaultUrl();
        this.totalPostCount = totalPostCount;
        this.totalFlowerCount = totalFlowerCount;
        this.totalFlowingCount = totalFlowingCount;
        this.createdMonthYear = formatCreatedDate(member.getCreatedDate());
    }

    private String formatCreatedDate(LocalDateTime createdDate) {
        return createdDate.format(DATE_FORMATTER);
    }
}
