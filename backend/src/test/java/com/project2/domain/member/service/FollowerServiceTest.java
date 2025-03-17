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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
public class FollowerServiceTest {

	@Mock
	private FollowRepository followRepository;

	@Mock
	private MemberService memberService;

	@Mock
	private Rq rq;

	@InjectMocks
	private FollowerService followerService;

	private Member user;
	private Member follower1;
	private Member follower2;

	@BeforeEach
	public void setUp() {
		// Create test members
		user = new Member();
		user.setId(1L);
		user.setNickname("testUser");

		follower1 = new Member();
		follower1.setId(2L);
		follower1.setNickname("follower1");

		follower2 = new Member();
		follower2.setId(3L);
		follower2.setNickname("follower2");
	}

    @Test
    public void testGetFollowers_Success() {
        when(rq.getActor()).thenReturn(user);
        when(memberService.findByIdOrThrow(1L)).thenReturn(user);

        Follows follow1 = new Follows();
        follow1.setFollower(follower1);
        follow1.setFollowing(user);

        Follows follow2 = new Follows();
        follow2.setFollower(follower2);
        follow2.setFollowing(user);

        List<Follows> followsList = Arrays.asList(follow1, follow2);
        Page<Follows> followsPage = new PageImpl<>(followsList);

        when(followRepository.findByFollowing(eq(user), any(Pageable.class))).thenReturn(followsPage);

        Page<FollowerResponseDto> followers = followerService.getFollowers(1L, Pageable.unpaged());

        assertNotNull(followers);
        assertEquals(2, followers.getContent().size());

        assertTrue(followers.getContent().stream().anyMatch(f -> f.getUserId().equals(follower1.getId())));
        assertTrue(followers.getContent().stream().anyMatch(f -> f.getUserId().equals(follower2.getId())));

        verify(rq).getActor();
        verify(memberService).findByIdOrThrow(1L);
        verify(followRepository).findByFollowing(eq(user), any(Pageable.class));
    }
    @Test
    public void testGetFollowers_NoFollowers() {
        when(rq.getActor()).thenReturn(user);
        when(memberService.findByIdOrThrow(1L)).thenReturn(user);

        Page<Follows> emptyPage = Page.empty();
        when(followRepository.findByFollowing(eq(user), any(Pageable.class))).thenReturn(emptyPage);

        Page<FollowerResponseDto> followers = followerService.getFollowers(1L, Pageable.unpaged());

        assertNotNull(followers);
        assertTrue(followers.isEmpty());

        verify(rq).getActor();
        verify(memberService).findByIdOrThrow(1L);
        verify(followRepository).findByFollowing(eq(user), any(Pageable.class));
    }

    @Test
    public void testGetFollowers_UserNotFound() {
        when(rq.getActor()).thenReturn(user);
        when(memberService.findByIdOrThrow(1L)).thenThrow(new ServiceException("404", "사용자를 찾을 수 없습니다."));

        ServiceException exception = assertThrows(ServiceException.class, () -> {
            followerService.getFollowers(1L, Pageable.unpaged());
        });

        assertEquals("404", exception.getCode());
        assertEquals("사용자를 찾을 수 없습니다.", exception.getMessage());

        verify(rq).getActor();
        verify(memberService).findByIdOrThrow(1L);
    }

	@Test
	@DisplayName("회원이 존재할 경우 팔로워 수를 정상적으로 반환해야 한다")
	public void testGetFollowerCount_Success() {
		// Given
		long memberId = 1L;
		when(followRepository.countByFollowing(user)).thenReturn(5L);

		// When
		long followerCount = followerService.getFollowersCount(user);

		// Then
		assertEquals(5L, followerCount);
		verify(followRepository).countByFollowing(user);
	}
}
