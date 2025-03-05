package com.project2.domain.member.service;

import com.project2.domain.member.dto.FollowerResponseDto;
import com.project2.domain.member.entity.Follows;
import com.project2.domain.member.entity.Member;
import com.project2.domain.member.repository.FollowRepository;
import com.project2.domain.member.repository.MemberRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
@Service
@RequiredArgsConstructor
public class FollowerService {
    private final FollowRepository followRepository;
    private final MemberRepository memberRepository;

    public List<FollowerResponseDto> getFollowers(Long userId) {

        Member user = findMemberById(userId);

        // user를 팔로우하는 모든 Follows 리스트를 가져옴
        List<Follows> followsList = followRepository.findByFollowing(user);

        return followsList.stream()
                .map(follow -> FollowerResponseDto.fromEntity(follow.getFollower()))
                .collect(Collectors.toList());
    }

    private Member findMemberById(Long userId) {
        // userId로 Member 엔티티를 가져오는 로직 구현
        return memberRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Member not found"));
    }
}
