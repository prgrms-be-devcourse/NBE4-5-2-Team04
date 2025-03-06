package com.project2.domain.member.service;

import com.project2.domain.member.dto.FollowerResponseDto;
import com.project2.domain.member.entity.Follows;
import com.project2.domain.member.entity.Member;
import com.project2.domain.member.repository.FollowRepository;
import com.project2.domain.member.repository.MemberRepository;
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
        when(memberRepository.findById(1L)).thenReturn(Optional.of(user));

        // Create Follows entities
        Follows follow1 = new Follows();
        follow1.setFollower(user); // Set the user as the follower
        follow1.setFollowing(following1); // Set following1 as the following

        Follows follow2 = new Follows();
        follow2.setFollower(user); // Set the user as the follower
        follow2.setFollowing(following2); // Set following2 as the following

        when(followRepository.findByFollower(user)).thenReturn(Arrays.asList(follow1, follow2));

        // When
        List<FollowerResponseDto> followings = followingService.getFollowings(1L);

        // Then
        assertNotNull(followings);
        assertEquals(2, followings.size());

        // Verify the followings match the mocked data
        assertTrue(followings.stream().anyMatch(f -> f.getUserId().equals(following1.getId())));
        assertTrue(followings.stream().anyMatch(f -> f.getUserId().equals(following2.getId())));

        // Verify interactions
        verify(memberRepository).findById(1L);
        verify(followRepository).findByFollower(user);
    }

    @Test
    public void testGetFollowings_NoFollowings() {
        // Given
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
        when(memberRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(EntityNotFoundException.class, () -> {
            followingService.getFollowings(1L);
        });
    }
}
