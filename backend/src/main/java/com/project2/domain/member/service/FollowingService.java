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
public class FollowingService {

    private final FollowRepository followRepository;
    private final MemberRepository memberRepository;

    public List<FollowerResponseDto> getFollowings(Long userId) {

        Member user = findMemberById(userId);

        List<Follows> followsList = followRepository.findByFollower(user);

        return followsList.stream()
                .map(follow -> FollowerResponseDto.fromEntity(follow.getFollowing()))
                .collect(Collectors.toList());
    }

    private Member findMemberById(Long userId) {

        return memberRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Member not found"));
    }
}
