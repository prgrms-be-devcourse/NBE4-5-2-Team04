package com.project2.domain.member.service;

import com.project2.domain.member.entity.Member;
import com.project2.domain.member.dto.FollowingPostResponseDto;
import com.project2.domain.member.enums.Provider;
import com.project2.domain.post.entity.Post;
import com.project2.domain.post.repository.PostRepository;
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

    @InjectMocks
    private FollowingPostService followingPostService;

    private Member user;
    private Pageable pageable;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        user = new Member(); // Member 객체 초기화
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
        post1.setMember(new Member(1L,"test@test.com" ,"User1", "profileImageUrl1", Provider.GOOGLE));


        post2.setId(2L);
        post2.setTitle("Title 2");
        post2.setContent("Content 2");
        post2.setMember(new Member(2L, "test2@test.com","User2", "profileImageUrl2",Provider.NAVER));


        List<Post> postList = List.of(post1, post2);
        Page<Post> postPage = new PageImpl<>(postList, pageable, postList.size());

        when(postRepository.findPostsByFollowing(user, pageable)).thenReturn(postPage);

        // When
        Page<FollowingPostResponseDto> result = followingPostService.getFollowingPosts(user, pageable);

        // Then
        assertEquals(2, result.getContent().size());
        FollowingPostResponseDto dto1 = result.getContent().get(0);
        FollowingPostResponseDto dto2 = result.getContent().get(1);

        assertEquals(post1.getId(), dto1.getPostId());
        assertEquals(post1.getTitle(), dto1.getTitle());
        assertEquals(post1.getContent(), dto1.getContent());
        assertEquals(post1.getMember().getId(), dto1.getUserId());
        assertEquals(post1.getMember().getNickname(), dto1.getNickname());

        assertEquals(post2.getId(), dto2.getPostId());
        assertEquals(post2.getTitle(), dto2.getTitle());
        assertEquals(post2.getContent(), dto2.getContent());
        assertEquals(post2.getMember().getId(), dto2.getUserId());
        assertEquals(post2.getMember().getNickname(), dto2.getNickname());
    }
}


