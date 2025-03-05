package com.project2.domain.member.service;

import com.project2.domain.member.dto.FollowRequestDto;
import com.project2.domain.member.dto.FollowResponseDto;
import com.project2.domain.member.entity.Follows;
import com.project2.domain.member.entity.Member;
import com.project2.domain.member.repository.FollowRepository;
import com.project2.domain.member.repository.MemberRepository;
import com.project2.global.exception.ServiceException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class FollowServiceTest {

    @Mock
    private FollowRepository followRepository;

    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private FollowService followService;

    private Member follower;
    private Member following;
    private FollowRequestDto requestDto;

    @BeforeEach
    public void setUp() {
        follower = new Member();
        follower.setId(1L);


        following = new Member();
        following.setId(2L);

        Follows follows = new Follows();
        follows.setFollower(follower);
        follows.setFollowing(following);

        requestDto = new FollowRequestDto(follows);
    }

    @Test
    public void testToggleFollow_Success_Follow() {
// given
        when(memberRepository.findById(1L)).thenReturn(Optional.of(follower));
        when(memberRepository.findById(2L)).thenReturn(Optional.of(following));
        when(followRepository.findByFollowerAndFollowing(follower, following)).thenReturn(Optional.empty());
        when(followRepository.save(any(Follows.class))).thenReturn(new Follows(1L, follower, following));


// when
        FollowResponseDto responseDto = followService.toggleFollow(1L, requestDto); // userid로 변경

// then
        assertNotNull(responseDto);
        assertEquals(1L, responseDto.getId());
        assertEquals(1L, responseDto.getFollowerId());
        assertEquals(2L, responseDto.getFollowingId());
    }

    @Test
    public void testToggleFollow_Success_Unfollow() {
// given
        when(memberRepository.findById(1L)).thenReturn(Optional.of(follower));
        when(memberRepository.findById(2L)).thenReturn(Optional.of(following));
        when(followRepository.findByFollowerAndFollowing(follower, following)).thenReturn(Optional.of(new Follows(1L, follower, following)));


// when
        FollowResponseDto responseDto = followService.toggleFollow(1L, requestDto); // userid로 변경

// then
        assertNull(responseDto);
    }

    @Test
    public void testToggleFollow_ServiceException_FollowerNotFound() {
// given
        when(memberRepository.findById(1L)).thenReturn(Optional.empty());


// when & then
        ServiceException exception = assertThrows(ServiceException.class, () -> followService.toggleFollow(1L, requestDto)); // userid로 변경
        assertEquals(String.valueOf(HttpStatus.NOT_FOUND.value()), exception.getCode());
        assertEquals("팔로워를 찾을 수 없습니다.", exception.getMsg());
    }

    @Test
    public void testToggleFollow_ServiceException_FollowingNotFound() {
// given
        when(memberRepository.findById(1L)).thenReturn(Optional.of(follower));
        when(memberRepository.findById(2L)).thenReturn(Optional.empty());


// when & then
        ServiceException exception = assertThrows(ServiceException.class, () -> followService.toggleFollow(1L, requestDto)); // userid로 변경
        assertEquals(String.valueOf(HttpStatus.NOT_FOUND.value()), exception.getCode());
        assertEquals("팔로잉을 찾을 수 없습니다.", exception.getMsg());
    }
}