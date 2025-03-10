package com.project2.domain.post.repository;

import com.project2.domain.post.dto.toggle.ScrapResponseDTO;
import com.project2.domain.post.entity.Scrap;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ScrapRepository extends JpaRepository<Scrap, Long> {

    @Modifying
    @Query("""
        DELETE FROM Scrap s
        WHERE s.post.id = :postId AND s.member.id = :memberId
    """)
    int toggleScrapIfExists(@Param("postId") Long postId, @Param("memberId") Long memberId);

    @Query("""
        SELECT new com.project2.domain.post.dto.toggle.ScrapResponseDTO(
            CASE WHEN COUNT(s) > 0 THEN true ELSE false END,
            (SELECT COUNT(s2) FROM Scrap s2 WHERE s2.post.id = :postId)
        )
        FROM Scrap s
        WHERE s.post.id = :postId AND s.member.id = :memberId
    """)
    ScrapResponseDTO getScrapStatus(@Param("postId") Long postId, @Param("memberId") Long memberId);
}