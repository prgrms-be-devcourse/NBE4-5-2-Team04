package com.project2.domain.post.repository;

import com.project2.domain.post.dto.comment.CommentResponseDTO;
import com.project2.domain.post.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    @Query(
        """
        SELECT new com.project2.domain.post.dto.comment.CommentResponseDTO(
            c.id, c.content, c.member.nickname)
        FROM Comment c WHERE c.post.id = :postId ORDER BY c.id ASC
        """)
    List<CommentResponseDTO> findByPostId(@Param("postId") Long postId);


    @Modifying
    @Query(
            """
            DELETE FROM Comment c WHERE c.id = :commentId AND c.member.id = :memberId
            """
    )
    int deleteByIdAndMemberId(@Param("commentId") Long commentId, @Param("memberId") Long memberId);
}