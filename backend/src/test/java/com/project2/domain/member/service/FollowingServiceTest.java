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
public class FollowingServiceTest {

    @Mock
    private FollowRepository followRepository;

    @Mock
    private MemberRepository memberRepository;

    @Mock private Rq rq;

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
        when(memberRepository.findById(1L)).thenReturn(Optional.of(user));

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


        verify(memberRepository).findById(1L);
        verify(followRepository).findByFollower(user);
    }

    @Test
    public void testGetFollowings_NoFollowings() {
        // Given
        when(rq.getActor()).thenReturn(user);
        when(memberRepository.findById(1L)).thenReturn(Optional.of(user));
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
        when(memberRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(EntityNotFoundException.class, () -> {
            followingService.getFollowings(1L);
        });
    }
}
