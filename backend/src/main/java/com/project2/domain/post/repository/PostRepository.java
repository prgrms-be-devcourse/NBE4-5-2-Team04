package com.project2.domain.post.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.project2.domain.post.entity.Post;

public interface PostRepository extends JpaRepository<Post, Long> {
}
