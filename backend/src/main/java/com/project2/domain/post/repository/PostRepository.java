package com.project2.domain.post.repository;

import com.project2.domain.member.entity.Member;
import com.project2.domain.post.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PostRepository extends JpaRepository<Post, Long> {

    @Query("""
    SELECT p FROM Post p
    WHERE p.member IN (
        SELECT f.following FROM Follows f WHERE f.follower = :user
    )
    ORDER BY p.createdDate DESC
""")
    Page<Post> findPostsByFollowing(@Param("user") Member user, Pageable pageable);

}