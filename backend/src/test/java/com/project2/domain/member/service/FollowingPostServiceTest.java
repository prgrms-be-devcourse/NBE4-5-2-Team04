package com.project2.domain.member.service;

import com.project2.domain.member.entity.Member;
import com.project2.domain.member.dto.FollowingPostResponseDto;
import com.project2.domain.member.enums.Provider;
import com.project2.domain.post.entity.Post;
import com.project2.domain.post.repository.PostRepository;
import com.project2.global.security.Rq;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

public class FollowingPostServiceTest {

    @Mock
    private PostRepository postRepository;

    @Mock private Rq rq;

    @InjectMocks
    private FollowingPostService followingPostService;

    private Member user;
    private Pageable pageable;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        user = new Member(1L, "test@test.com", "User1", "profileImageUrl1", Provider.GOOGLE);
        pageable = Pageable.ofSize(10); // 페이지 크기 설정
    }

    @Test
    public void testGetFollowingPosts() {

        // Given
        Post post1 = new Post(); // Post 객체 초기화
        Post post2 = new Post(); // Post 객체 초기화
        post1.setId(1L);
        post1.setTitle("Title 1");
        post1.setContent("Content 1");
        post1.setMember(new Member(1L, "test@test.com", "User1", "profileImageUrl1", Provider.GOOGLE)); // Ensure ID is set

        post2.setId(2L);
        post2.setTitle("Title 2");
        post2.setContent("Content 2");
        post2.setMember(new Member(2L, "test2@test.com", "User2", "profileImageUrl2", Provider.NAVER)); // Ensure ID is set

        List<Post> postList = List.of(post1, post2);
        Page<Post> postPage = new PageImpl<>(postList, pageable, postList.size());

        when(rq.getActor()).thenReturn(user);
        when(postRepository.findPostsByFollowing(user, pageable)).thenReturn(postPage);

        // When
        Page<FollowingPostResponseDto> result = followingPostService.getFollowingPosts(user, pageable);

        // Then
        assertEquals(2, result.getContent().size());
        FollowingPostResponseDto dto1 = result.getContent().get(0);
        FollowingPostResponseDto dto2 = result.getContent().get(1);



    }

}


