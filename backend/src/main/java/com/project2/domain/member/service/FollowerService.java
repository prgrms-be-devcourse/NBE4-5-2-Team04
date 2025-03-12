package com.project2.domain.member.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project2.domain.member.dto.FollowerResponseDto;
import com.project2.domain.member.entity.Follows;
import com.project2.domain.member.entity.Member;
import com.project2.domain.member.repository.FollowRepository;
import com.project2.global.exception.ServiceException;
import com.project2.global.security.Rq;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FollowerService {
	private final FollowRepository followRepository;
	private final MemberService memberService;
	private final Rq rq;

	public List<FollowerResponseDto> getFollowers(Long memberId) {
		Member actor = rq.getActor(); //
		if (!actor.getId().equals(memberId)) {
			throw new ServiceException("403", "자신의 팔로워 목록만 볼 수 있습니다.");
		}

		Member member = memberService.findByIdOrThrow(memberId);

		List<Follows> follows = followRepository.findByFollowing(member);
		return follows.stream()
			.map(follow -> FollowerResponseDto.fromEntity(follow.getFollower()))
			.collect(Collectors.toList());
	}

	@Transactional(readOnly = true)
	public long getFollowersCount(Member member) {
		return followRepository.countByFollower(member);
	}
}
