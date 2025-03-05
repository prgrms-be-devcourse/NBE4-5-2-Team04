package com.project2.domain.post.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.project2.domain.post.entity.PostImage;

public interface PostImageRepository extends JpaRepository<PostImage, Long> {
}
