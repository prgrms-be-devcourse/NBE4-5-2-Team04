package com.project2.domain.post.mapper;

import com.project2.domain.member.entity.Member;
import com.project2.domain.post.entity.Likes;
import com.project2.domain.post.entity.Post;
import com.project2.domain.post.entity.Scrap;
import org.springframework.stereotype.Component;

@Component
public class ToggleMapper {

    public Likes toLikes(Long userId, Long postId) {
        return Likes.builder()
                .post(Post.builder().id(postId).build())
                .member(Member.builder().id(userId).build())
                .build();
    }

    public Scrap toScrap(Long userId, Post post) {
        return Scrap.builder()
                .post(post)
                .member(Member.builder().id(userId).build())
                .build();
    }
}