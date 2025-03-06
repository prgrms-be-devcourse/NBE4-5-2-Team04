package com.project2.domain.member.service;

import com.project2.domain.member.entity.Member;
import com.project2.domain.member.dto.FollowingPostResponseDto;
import com.project2.domain.post.entity.Post;
import com.project2.domain.post.repository.PostRepository;
import com.project2.global.exception.ServiceException;
import com.project2.global.security.Rq;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FollowingPostService {

    private final PostRepository postRepository;
    private final Rq rq;

    public Page<FollowingPostResponseDto> getFollowingPosts(Member user, Pageable pageable) {

        Member actor = rq.getActor();

        // actor와 user가 동일한지 확인
        if (!actor.getId().equals(user.getId())) {
            throw new ServiceException("403","자신의 팔로잉 게시물만 볼 수 있습니다.");
        }
        Page<Post> posts = postRepository.findPostsByFollowing(user, pageable);
        return posts.map(FollowingPostResponseDto::fromEntity);
    }
}
