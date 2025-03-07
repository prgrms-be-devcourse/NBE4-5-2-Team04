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
public class FollowingService {

    private final FollowRepository followRepository;
    private final MemberRepository memberRepository;
    private final Rq rq;

    public List<FollowerResponseDto> getFollowings(Long memberId) {

        Member actor = rq.getActor();
        if (!actor.getId().equals(memberId)) {
            throw new ServiceException("403","자신의 팔로잉 목록만 볼 수 있습니다.");
        }
        Member member = findMemberById(memberId);
        List<Follows> followsList = followRepository.findByFollower(member);

        return followsList.stream()
                .map(follow -> FollowerResponseDto.fromEntity(follow.getFollowing()))
                .collect(Collectors.toList());
    }

    private Member findMemberById(Long memberId) {

        return memberRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException("Member not found"));
    }
}
