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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class FollowServiceTest {

    @Mock
    private FollowRepository followRepository;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private Rq rq;

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
    @DisplayName("팔로우 성공")
    public void testToggleFollow_Success_Follow() {
        // given
        when(rq.getActor()).thenReturn(follower);
        when(memberRepository.findById(2L)).thenReturn(Optional.of(following));
        when(followRepository.findByFollowerAndFollowing(follower, following)).thenReturn(Optional.empty());
        when(followRepository.save(any(Follows.class))).thenReturn(new Follows(1L, follower, following));

        // when
        RsData<FollowResponseDto> response = followService.toggleFollow(requestDto);

        // then
        assertEquals(1L, response.getData().getFollowerId());
        assertEquals(2L, response.getData().getFollowingId());
    }

    @Test
    @DisplayName("언팔로우 성공")
    public void testToggleFollow_Success_Unfollow() {
        // given
        when(rq.getActor()).thenReturn(follower);
        when(memberRepository.findById(2L)).thenReturn(Optional.of(following));
        when(followRepository.findByFollowerAndFollowing(follower, following)).thenReturn(Optional.of(new Follows(1L, follower, following)));

        // when
        RsData<FollowResponseDto> response = followService.toggleFollow(requestDto);

        // then
        assertThat(response.getCode()).isEqualTo("204");
    }
    }