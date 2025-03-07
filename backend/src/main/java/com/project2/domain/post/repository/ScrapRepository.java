package com.project2.domain.post.repository;

import com.project2.domain.post.entity.Scrap;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

//@Repository
//public interface ScrapRepository extends JpaRepository<Scrap, Long> {
//
//    @Modifying
//    @Query("""
//        DELETE FROM Scrap s
//        WHERE s.post.id = :postId AND s.member.id = :memberId
//    """)
//    int toggleScrapIfExists(@Param("postId") Long postId, @Param("memberId") Long memberId);
//
//    @Query("""
//        SELECT new com.project2.domain.post.dto.PostListResponseDTO(
//            p.id, p.title, p.member.nickname, p.place.name,
//            (SELECT pi.imageUrl FROM PostImage pi WHERE pi.post.id = p.id ORDER BY pi.id ASC LIMIT 1),
//            (SELECT COUNT(l) FROM Likes l WHERE l.post.id = p.id),
//            (SELECT COUNT(s) FROM Scrap s WHERE s.post.id = p.id),
//            (SELECT COUNT(c) FROM Comment c WHERE c.post.id = p.id),
//            CASE WHEN :memberId IN (SELECT s.member.id FROM Scrap s WHERE s.post.id = p.id) THEN true ELSE false END,
//            CASE WHEN :memberId IN (SELECT f.follower.id FROM Follows f WHERE f.following.id = p.member.id) THEN true ELSE false END
//        )
//        FROM Scrap s
//        JOIN s.post p
//        WHERE s.member.id = :memberId
//    """)
//    Page<PostListResponseDTO> findScrappedPostsByMember(@Param("memberId") Long memberId, Pageable pageable);
//}