package com.project2.domain.post.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.project2.domain.post.dto.toggle.LikeResponseDTO;
import com.project2.domain.post.entity.Likes;

@Repository
public interface LikesRepository extends JpaRepository<Likes, Long> {

	@Modifying
	@Query(
		"""
			DELETE FROM Likes l
			WHERE l.post.id = :postId AND l.member.id = :memberId
			""")
	int toggleLikeIfExists(@Param("postId") Long postId, @Param("memberId") Long memberId);

	@Query(
		"""
			SELECT new com.project2.domain.post.dto.toggle.LikeResponseDTO(
			    CASE WHEN COUNT(l) > 0 THEN true ELSE false END,
			    CAST(COUNT(l2) AS integer)
			)
			FROM Likes l
			LEFT JOIN Likes l2 ON l2.post.id = l.post.id
			WHERE l.post.id = :postId AND l.member.id = :memberId
			"""
	)
	LikeResponseDTO getLikeStatus(@Param("postId") Long postId, @Param("memberId") Long memberId);

	@Query("SELECT COUNT(l) FROM Likes l WHERE l.post.id = :postId")
	int countByPostId(@Param("postId") Long postId);

	@Query("SELECT COUNT(l) > 0 FROM Likes l WHERE l.post.id = :postId AND l.member.id = :memberId")
	boolean existsByPostIdAndMemberId(@Param("postId") Long postId, @Param("memberId") Long memberId);

}