package com.project2.domain.post.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.project2.domain.post.entity.Post;

public interface PostRepository extends JpaRepository<Post, Long> {

	// 전체 게시글 조회
	@Query(value = """
		    SELECT p.id, p.title, p.content,
		        pl.name, pl.category,
		        (SELECT COUNT(l) FROM Likes l WHERE l.post.id = p.id) lc,
		        (SELECT COUNT(s) FROM Scrap s WHERE s.post.id = p.id) sc,
		        (SELECT COUNT(c) FROM Comment c WHERE c.post.id = p.id) cc,
		        (SELECT GROUP_CONCAT(pi.imageUrl, ',') FROM PostImage pi WHERE pi.post.id = p.id),
		        m.id, m.nickname, m.profileImageUrl
		    
		    FROM Post p
		    INNER JOIN p.member m
		    INNER JOIN p.place pl
		    WHERE
		            (:placeName IS NULL OR pl.name LIKE CONCAT('%', :placeName, '%'))
		            AND (:placeCategory IS NULL OR pl.category = :placeCategory)
		    ORDER BY
		        CASE
		            WHEN :sortBy = 'likes' THEN lc
		            WHEN :sortBy = 'scrap' THEN sc
		            ELSE p.createdDate
		        END DESC,
		        p.createdDate DESC
		""",
		countQuery = "SELECT COUNT(*) FROM Post p WHERE (:placeName IS NULL OR p.place_id IN (SELECT place_id FROM Place WHERE name LIKE CONCAT('%', :placeName, '%'))) AND (:placeCategory IS NULL OR p.place_id IN (SELECT place_id FROM Place WHERE category = :placeCategory))",
		nativeQuery = true)
	Page<Object[]> findAllBySearchWordsAndSort(
		@Param("sortBy") String sortBy,
		@Param("placeName") String placeName,
		@Param("placeCategory") String placeCategory,
		Pageable pageable
	);

	// 사용자가 좋아요 누른 게시글 조회
	@Query(value = """
		SELECT p.id, p.title, p.content,
		 pl.name AS place_name, pl.category AS place_category,
		       (SELECT COUNT(*) FROM Likes l WHERE l.post_id = p.id) AS like_count,
		       (SELECT COUNT(*) FROM Scrap s WHERE s.post_id = p.id) AS scrap_count,
		       (SELECT COUNT(*) FROM Comment c WHERE c.post_id = p.id) AS comment_count,
		       (SELECT GROUP_CONCAT(pi.image_url SEPARATOR ',') FROM PostImage pi WHERE pi.post_id = p.id) AS image_urls,
		       m.user_id AS member_id, m.nickname, m.profile_image_url
		FROM Post p
		INNER JOIN Member m ON p.member_id = m.user_id
		INNER JOIN Place pl ON p.place_id = pl.place_id
		WHERE p.id IN (SELECT post_id FROM Likes WHERE member_id = :memberId)
		ORDER BY p.created_at DESC
		""",
		countQuery = "SELECT COUNT(*) FROM Post p WHERE p.id IN (SELECT post_id FROM Likes WHERE member_id = :memberId)",
		nativeQuery = true)
	Page<Object[]> findLikedPosts(@Param("memberId") Long memberId, Pageable pageable);

	// 사용자가 스크랩한 게시글 조회
	@Query(value = """
		SELECT p.id, p.title, p.content,
		 pl.name AS place_name, pl.category AS place_category,
		       (SELECT COUNT(*) FROM Likes l WHERE l.post_id = p.id) AS like_count,
		       (SELECT COUNT(*) FROM Scrap s WHERE s.post_id = p.id) AS scrap_count,
		       (SELECT COUNT(*) FROM Comment c WHERE c.post_id = p.id) AS comment_count,
		       (SELECT GROUP_CONCAT(pi.image_url SEPARATOR ',') FROM PostImage pi WHERE pi.post_id = p.id) AS image_urls,
		       m.user_id AS member_id, m.nickname, m.profile_image_url
		FROM Post p
		INNER JOIN Member m ON p.member_id = m.user_id
		INNER JOIN Place pl ON p.place_id = pl.place_id
		WHERE p.id IN (SELECT post_id FROM Scrap WHERE member_id = :memberId)
		ORDER BY p.created_at DESC
		""",
		countQuery = "SELECT COUNT(*) FROM Post p WHERE p.id IN (SELECT post_id FROM Scrap WHERE member_id = :memberId)",
		nativeQuery = true)
	Page<Object[]> findScrappedPosts(@Param("memberId") Long memberId, Pageable pageable);

	// 팔로워들의 게시글 조회
	@Query(value = """
		SELECT p.id, p.title, p.content,
		 pl.name AS place_name, pl.category AS place_category,
		       (SELECT COUNT(*) FROM Likes l WHERE l.post_id = p.id) AS like_count,
		       (SELECT COUNT(*) FROM Scrap s WHERE s.post_id = p.id) AS scrap_count,
		       (SELECT COUNT(*) FROM Comment c WHERE c.post_id = p.id) AS comment_count,
		       (SELECT GROUP_CONCAT(pi.image_url SEPARATOR ',') FROM PostImage pi WHERE pi.post_id = p.id) AS image_urls,
		       m.user_id AS member_id, m.nickname, m.profile_image_url
		FROM Post p
		INNER JOIN Member m ON p.member_id = m.user_id
		INNER JOIN Place pl ON p.place_id = pl.place_id
		WHERE p.member_id IN (SELECT following_id FROM Follow WHERE follower_id = :memberId)
		ORDER BY p.created_at DESC
		""",
		countQuery = "SELECT COUNT(*) FROM Post p WHERE p.member_id IN (SELECT following_id FROM Follow WHERE follower_id = :memberId)",
		nativeQuery = true)
	Page<Object[]> findFollowerPosts(@Param("memberId") Long memberId, Pageable pageable);

	// 특정 사용자의 게시글 조회
	@Query(value = """
		SELECT p.id, p.title, p.content,
		 pl.name AS place_name, pl.category AS place_category,
		       (SELECT COUNT(*) FROM Likes l WHERE l.post_id = p.id) AS like_count,
		       (SELECT COUNT(*) FROM Scrap s WHERE s.post_id = p.id) AS scrap_count,
		       (SELECT COUNT(*) FROM Comment c WHERE c.post_id = p.id) AS comment_count,
		       (SELECT GROUP_CONCAT(pi.image_url SEPARATOR ',') FROM PostImage pi WHERE pi.post_id = p.id) AS image_urls,
		       m.user_id AS member_id, m.nickname, m.profile_image_url
		FROM Post p
		INNER JOIN Member m ON p.member_id = m.user_id
		INNER JOIN Place pl ON p.place_id = pl.place_id
		WHERE p.member_id = :targetMemberId
		ORDER BY p.created_at DESC
		""",
		countQuery = "SELECT COUNT(*) FROM Post p WHERE p.member_id = :targetMemberId",
		nativeQuery = true)
	Page<Object[]> findPostsByMember(@Param("targetMemberId") Long targetMemberId, Pageable pageable);

	// 특정 장소내의 게시글 조회
	@Query(value = """
		SELECT p.id, p.title, p.content,
		 pl.name AS place_name, pl.category AS place_category,
		       (SELECT COUNT(*) FROM Likes l WHERE l.post_id = p.id) AS like_count,
		       (SELECT COUNT(*) FROM Scrap s WHERE s.post_id = p.id) AS scrap_count,
		       (SELECT COUNT(*) FROM Comment c WHERE c.post_id = p.id) AS comment_count,
		       (SELECT GROUP_CONCAT(pi.image_url SEPARATOR ',') FROM PostImage pi WHERE pi.post_id = p.id) AS image_urls,
		       m.user_id AS member_id, m.nickname, m.profile_image_url
		FROM Post p
		INNER JOIN Member m ON p.member_id = m.user_id
		INNER JOIN Place pl ON p.place_id = pl.place_id
		WHERE pl.id = :targetPlaceId
		ORDER BY p.created_at DESC
		""",
		countQuery = "SELECT COUNT(*) FROM Post p WHERE p.member_id = :targetPlaceId",
		nativeQuery = true)
	Page<Object[]> findPostsByPlace(@Param("targetMemberId") Long targetPlaceId, Pageable pageable);

	// 단일 게시글 상세 조회
	@Query(value = """
		SELECT p.id, p.title, p.content,
		 pl.name AS place_name, pl.category AS place_category,
		       (SELECT COUNT(*) FROM Likes l WHERE l.post_id = p.id) AS like_count,
		       (SELECT COUNT(*) FROM Scrap s WHERE s.post_id = p.id) AS scrap_count,
		       EXISTS (SELECT 1 FROM Likes l WHERE l.post_id = p.id AND l.member_id = :memberId) AS is_liked,
		       EXISTS (SELECT 1 FROM Scrap s WHERE s.post_id = p.id AND s.member_id = :memberId) AS is_scrapped,
		       (SELECT GROUP_CONCAT(pi.image_url SEPARATOR ',') FROM PostImage pi WHERE pi.post_id = p.id) AS image_urls,
		       m.user_id AS member_id, m.nickname, m.profile_image_url
		FROM Post p
		INNER JOIN Member m ON p.member_id = m.user_id
		INNER JOIN Place pl ON p.place_id = pl.place_id
		WHERE p.id = :postId
		""",
		nativeQuery = true)
	Optional<Object[]> findPostDetailById(@Param("postId") Long postId, @Param("memberId") Long memberId);
}
