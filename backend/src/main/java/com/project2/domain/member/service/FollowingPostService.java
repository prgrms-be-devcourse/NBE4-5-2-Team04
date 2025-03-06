package com.project2.domain.member.service;

import com.project2.domain.member.entity.Member;
import com.project2.domain.member.dto.FollowingPostResponseDto;
import com.project2.domain.post.entity.Post;
import com.project2.domain.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FollowingPostService {

    private final PostRepository postRepository;


    public Page<FollowingPostResponseDto> getFollowingPosts(Member user, Pageable pageable) {
        Page<Post> posts = postRepository.findPostsByFollowing(user, pageable);
        return posts.map(FollowingPostResponseDto::fromEntity);
    }
}
