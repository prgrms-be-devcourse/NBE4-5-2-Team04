package com.project2.domain.member.service;

import com.project2.domain.member.dto.FollowerResponseDto;
import com.project2.domain.member.entity.Follows;
import com.project2.domain.member.entity.Member;
import com.project2.domain.member.repository.FollowRepository;
import com.project2.domain.member.repository.MemberRepository;
import com.project2.global.security.Rq;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class FollowerServiceTest {

    @Mock
    private FollowRepository followRepository;

    @Mock
    private MemberRepository memberRepository;

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
        // Given
        when(rq.getActor()).thenReturn(user);
        when(memberRepository.findById(1L)).thenReturn(Optional.of(user));

        // Create Follows entities
        Follows follow1 = new Follows();
        follow1.setFollower(follower1);
        follow1.setFollowing(user);

        Follows follow2 = new Follows();
        follow2.setFollower(follower2);
        follow2.setFollowing(user);

        when(followRepository.findByFollowing(user)).thenReturn(Arrays.asList(follow1, follow2));

        // When
        List<FollowerResponseDto> followers = followerService.getFollowers(1L);

        // Then
        assertNotNull(followers);
        assertEquals(2, followers.size());

        // Verify the followers match the mocked data
        assertTrue(followers.stream().anyMatch(f -> f.getUserId().equals(follower1.getId())));
        assertTrue(followers.stream().anyMatch(f -> f.getUserId().equals(follower2.getId())));

        // Verify interactions
        verify(rq).getActor(); // 실제로 getActor()가 호출되었는지 검증
        verify(memberRepository).findById(1L);
        verify(followRepository).findByFollowing(user);
    }

    @Test
    public void testGetFollowers_NoFollowers() {
        // Given
        when(rq.getActor()).thenReturn(user);  // 현재 로그인된 사용자
        when(memberRepository.findById(1L)).thenReturn(Optional.of(user));
        when(followRepository.findByFollowing(user)).thenReturn(Arrays.asList());

        // When
        List<FollowerResponseDto> followers = followerService.getFollowers(1L);

        // Then
        assertNotNull(followers);
        assertTrue(followers.isEmpty());

        // Verify interactions
        verify(rq).getActor();
        verify(memberRepository).findById(1L);
        verify(followRepository).findByFollowing(user);
    }

    @Test
    public void testGetFollowers_UserNotFound() {
        // Given
        when(rq.getActor()).thenReturn(user);  // 현재 로그인된 사용자
        when(memberRepository.findById(1L)).thenReturn(Optional.empty()); // 사용자 찾을 수 없음

        // When & Then
        assertThrows(EntityNotFoundException.class, () -> {
            followerService.getFollowers(1L);
        });

        // Verify interactions
        verify(rq).getActor();
        verify(memberRepository).findById(1L);
    }
}
