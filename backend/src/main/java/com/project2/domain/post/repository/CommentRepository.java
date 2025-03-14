package com.project2.domain.post.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.project2.domain.post.dto.comment.ListCommentResponseDTO;
import com.project2.domain.post.entity.Comment;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

	@Query("""
		SELECT new com.project2.domain.post.dto.comment.ListCommentResponseDTO(
		    c.id, c.content, c.member.nickname, c.parent.id)
		FROM Comment c WHERE c.post.id = :postId
		ORDER BY COALESCE(c.parent.id, c.id), c.id
		""")
	List<ListCommentResponseDTO> findByPostIdWithParentId(@Param("postId") Long postId);
}