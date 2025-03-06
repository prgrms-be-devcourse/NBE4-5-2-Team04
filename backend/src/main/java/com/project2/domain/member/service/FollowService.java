package com.project2.domain.member.service;

import com.project2.domain.member.dto.FollowRequestDto;
import com.project2.domain.member.dto.FollowResponseDto;
import com.project2.domain.member.entity.Follows;
import com.project2.domain.member.entity.Member;
import com.project2.domain.member.repository.FollowRepository;

import com.project2.domain.member.repository.MemberRepository;
import com.project2.global.dto.Empty;
import com.project2.global.dto.RsData;
import com.project2.global.exception.ServiceException;
import com.project2.global.security.Rq;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;


@Service
@RequiredArgsConstructor
public class FollowService {

    private final FollowRepository followRepository;
    private final MemberRepository memberRepository;
    private final Rq rq;

    @Transactional
    public RsData<FollowResponseDto> toggleFollow(FollowRequestDto requestDto) {
        Member actor = rq.getActor(); // 현재 사용자
        Member following = memberRepository.findById(requestDto.getFollowingId())
                .orElseThrow(() -> new ServiceException(
                        String.valueOf(HttpStatus.NOT_FOUND.value()),
                        "팔로잉을 찾을 수 없습니다."
                ));

        // followerId는 rq에서 가져오는 것으로 변경
        Member follower = actor; // 현재 사용자가 follower 역할을 함

        Optional<Follows> existingFollow = followRepository.findByFollowerAndFollowing(follower, following);

        if (existingFollow.isPresent()) {
            followRepository.delete(existingFollow.get());
            return new RsData<>("204", "언팔로우 되었습니다."); // 언팔로우 시에는 응답 데이터가 없을 수 있음
        } else {
            Follows newFollow = new Follows();
            newFollow.setFollower(follower);
            newFollow.setFollowing(following);
            Follows savedFollow = followRepository.save(newFollow);

            FollowResponseDto responseDto = new FollowResponseDto(savedFollow);
            responseDto.setId(savedFollow.getId());
            responseDto.setFollowerId(savedFollow.getFollower().getId());
            responseDto.setFollowingId(savedFollow.getFollowing().getId());

            return new RsData<>("200", "팔로우 되었습니다.", responseDto);
        }
    }
}
