package com.project2.domain.member.service;

import com.project2.domain.member.dto.FollowerResponseDto;
import com.project2.domain.member.entity.Follows;
import com.project2.domain.member.entity.Member;
import com.project2.domain.member.repository.FollowRepository;
import com.project2.domain.member.repository.MemberRepository;
import com.project2.global.exception.ServiceException;
import com.project2.global.security.Rq;
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
    private final Rq rq;

    public List<FollowerResponseDto> getFollowers(Long userId) {
        Member actor = rq.getActor(); //
        if (!actor.getId().equals(userId)) {
            throw new ServiceException("403","자신의 팔로워 목록만 볼 수 있습니다.");
        }

        Member user = memberRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        List<Follows> follows = followRepository.findByFollowing(user);
        return follows.stream()
                .map(follow -> FollowerResponseDto.fromEntity(follow.getFollower()))
                .collect(Collectors.toList());
    }


    private Member findMemberById(Long userId) {
        // userId로 Member 엔티티를 가져오는 로직 구현
        return memberRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Member not found"));
    }
}
