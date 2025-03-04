package com.project2.domain.member.service;

import com.project2.domain.member.dto.FollowRequestDto;
import com.project2.domain.member.dto.FollowResponseDto;
import com.project2.domain.member.entity.Follows;
import com.project2.domain.member.entity.Member;
import com.project2.domain.member.repository.FollowsRepository;
import com.project2.domain.member.repository.MemberRepository;
import com.project2.global.exception.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class FollowService {

    private final FollowsRepository followsRepository;
    private final MemberRepository memberRepository;

    @Autowired
    public FollowService(FollowsRepository followsRepository, MemberRepository memberRepository) {
        this.followsRepository = followsRepository;
        this.memberRepository = memberRepository;
    }

    @Transactional
    public FollowResponseDto toggleFollow(Member follower, FollowRequestDto requestDto) { // follower 매개변수 추가


        Member following = memberRepository.findById(requestDto.getFollowingId())
                .orElseThrow(() -> new ServiceException(
                        String.valueOf(HttpStatus.NOT_FOUND.value()),
                        "팔로잉을 찾을 수 없습니다."
                ));

        Optional<Follows> existingFollow = followsRepository.findByFollowerAndFollowing(follower, following);

        if (existingFollow.isPresent()) {
            followsRepository.delete(existingFollow.get());
            return null; // 언팔로우 시에는 응답 데이터가 없을 수 있음
        } else {
            Follows newFollow = new Follows();
            newFollow.setFollower(follower);
            newFollow.setFollowing(following);
            Follows savedFollow = followsRepository.save(newFollow);

            FollowResponseDto responseDto = new FollowResponseDto();
            responseDto.setId(savedFollow.getId());
            responseDto.setFollowerId(savedFollow.getFollower().getId());
            responseDto.setFollowingId(savedFollow.getFollowing().getId());

            return responseDto;
        }
    }
}
