package com.project2.domain.post.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.project2.domain.member.entity.Member;
import com.project2.domain.post.entity.Post;

public interface PostRepository extends JpaRepository<Post, Long>, JpaSpecificationExecutor<Post> {

	// 전체 게시글 조회
	@EntityGraph(attributePaths = {"place", "member", "likes", "scraps", "comments", "images"})
	Page<Post> findAll(Specification<Post> spec, Pageable pageable);

	// 사용자가 좋아요 누른 게시글 조회
	@Query("""
		    SELECT p FROM Post p
		    JOIN p.likes l
		    WHERE l.member.id = :memberId
		""")
	@EntityGraph(attributePaths = {"place", "member", "likes", "scraps", "comments", "images"})
	Page<Post> findLikedPosts(@Param("memberId") Long memberId, Pageable pageable);

	// 사용자가 스크랩한 게시글 조회
	@Query("""
		    SELECT p FROM Post p
		    JOIN p.scraps s
		    WHERE s.member.id = :memberId
		""")
	@EntityGraph(attributePaths = {"place", "member", "likes", "scraps", "comments", "images"})
	Page<Post> findScrappedPosts(@Param("memberId") Long memberId, Pageable pageable);

	// 팔로우하는 사람들의 게시글 조회
	@Query("""
		    SELECT p FROM Post p
		    WHERE p.member.id IN (
		        SELECT f.following.id FROM Follows f WHERE f.follower.id = :memberId
		    )
		""")
	@EntityGraph(attributePaths = {"place", "member", "likes", "scraps", "comments", "images"})
	Page<Post> findFollowingPosts(@Param("memberId") Long memberId, Pageable pageable);

	// 특정 사용자의 게시글 조회
	@Query("""
		    SELECT p FROM Post p
		    WHERE p.member.id = :targetMemberId
		""")
	@EntityGraph(attributePaths = {"place", "member", "likes", "scraps", "comments", "images"})
	Page<Post> findPostsByMember(@Param("targetMemberId") Long targetMemberId, Pageable pageable);

	// 특정 장소내의 게시글 조회
	@Query("""
		    SELECT p FROM Post p
		    JOIN p.place pl
		    WHERE pl.id = :targetPlaceId
		""")
	@EntityGraph(attributePaths = {"place", "member", "likes", "scraps", "comments", "images"})
	Page<Post> findPostsByPlace(@Param("targetPlaceId") Long targetPlaceId, Pageable pageable);

	// 단일 게시글 상세 조회
	@EntityGraph(attributePaths = {"place", "member", "likes", "scraps", "comments", "images"})
	Optional<Post> findById(Long id);

	long countByMember(Member actor);
}
