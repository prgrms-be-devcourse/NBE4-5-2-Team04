package com.project2.domain.member.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.project2.domain.member.dto.FollowerResponseDto;
import com.project2.domain.member.entity.Follows;
import com.project2.domain.member.entity.Member;
import com.project2.domain.member.repository.FollowRepository;
import com.project2.global.exception.ServiceException;
import com.project2.global.security.Rq;

@ExtendWith(MockitoExtension.class)
public class FollowingServiceTest {

	@Mock
	private FollowRepository followRepository;

	@Mock
	private MemberService memberService;

	@Mock
	private Rq rq;

	@InjectMocks
	private FollowingService followingService;

	private Member user;
	private Member following1;
	private Member following2;

	@BeforeEach
	public void setUp() {
		// Create test members
		user = new Member();
		user.setId(1L);
		user.setNickname("testUser");

		following1 = new Member();
		following1.setId(2L);
		following1.setNickname("follower1");

		following2 = new Member();
		following2.setId(3L);
		following2.setNickname("follower2");
	}

	@Test
	public void testGetFollowings_Success() {
		// Given
		when(rq.getActor()).thenReturn(user);
		when(memberService.findByIdOrThrow(1L)).thenReturn(user);

		Follows follow1 = new Follows();
		follow1.setFollower(user);
		follow1.setFollowing(following1);

		Follows follow2 = new Follows();
		follow2.setFollower(user);
		follow2.setFollowing(following2);

		when(followRepository.findByFollower(user)).thenReturn(Arrays.asList(follow1, follow2));

		// When
		List<FollowerResponseDto> followings = followingService.getFollowings(1L);

		// Then
		assertNotNull(followings);
		assertEquals(2, followings.size());

		assertTrue(followings.stream().anyMatch(f -> f.getUserId().equals(following1.getId())));
		assertTrue(followings.stream().anyMatch(f -> f.getUserId().equals(following2.getId())));

		verify(memberService).findByIdOrThrow(1L);
		verify(followRepository).findByFollower(user);
	}

	@Test
	public void testGetFollowings_NoFollowings() {
		// Given
		when(rq.getActor()).thenReturn(user);
		when(memberService.findByIdOrThrow(1L)).thenReturn(user);
		when(followRepository.findByFollower(user)).thenReturn(Arrays.asList());

		// When
		List<FollowerResponseDto> followings = followingService.getFollowings(1L);

		// Then
		assertNotNull(followings);
		assertTrue(followings.isEmpty());
	}

	@Test
	public void testGetFollowings_UserNotFound() {
		// Given
		when(rq.getActor()).thenReturn(user);
		when(memberService.findByIdOrThrow(1L)).thenThrow(new ServiceException("404", "사용자를 찾을 수 없습니다."));

		// When & Then
		ServiceException exception = assertThrows(ServiceException.class, () -> {
			followingService.getFollowings(1L);
		});

		// Then
		assertEquals("404", exception.getCode());
		assertEquals("사용자를 찾을 수 없습니다.", exception.getMessage());

		// Verify interactions
		verify(rq).getActor();
		verify(memberService).findByIdOrThrow(1L);
	}

	@Test
	@DisplayName("회원이 존재할 경우 팔로잉 수를 정상적으로 반환해야 한다")
	public void testGetFollowingsCount_Success() {
		// Given
		long memberId = 1L;
		when(followRepository.countByFollowing(user)).thenReturn(5L);

		// When
		long followerCount = followingService.getFollowingsCount(user);

		// Then
		assertEquals(5L, followerCount);
		verify(followRepository).countByFollowing(user);
	}
}
