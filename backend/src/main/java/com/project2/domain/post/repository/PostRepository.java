package com.project2.domain.post.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.project2.domain.post.dto.PostResponseDTO;
import com.project2.domain.post.entity.Post;

public interface PostRepository extends JpaRepository<Post, Long> {

	@Query(value = """
		    SELECT p.id, p.title, p.content, p.latitude, p.longitude,
		           COALESCE(COUNT(l.id), 0) as likeCount, COALESCE(COUNT(s.id), 0) as scrapCount, 
		           COALESCE(GROUP_CONCAT(pi.imageUrl SEPARATOR ','), '') AS imageUrls,
		           p.created_date, p.modified_date
		    FROM post p
		    LEFT JOIN likes l ON l.post_id = p.id
		    LEFT JOIN scrap s ON s.post_id = p.id
		    LEFT JOIN post_image pi ON pi.post_id = p.id
		    GROUP BY p.id 
		    ORDER BY 
		        CASE 
		            WHEN :sortBy = 'likes' THEN likeCount
		            WHEN :sortBy = 'scrap' THEN scrapCount
		        END DESC, 
		        p.created_date DESC
		""", nativeQuery = true)
	Page<PostResponseDTO> findAllOrderBySorted(@Param("sortBy") String sortBy, Pageable pageable);

	@Query(value = """
		    SELECT p.id, p.title, p.content, p.latitude, p.longitude,
		           COALESCE(COUNT(l.id), 0) as likeCount, COALESCE(COUNT(s.id), 0) as scrapCount, 
		           COALESCE(GROUP_CONCAT(pi.imageUrl SEPARATOR ','), '') AS imageUrls,
		           p.created_date, p.modified_date
		    FROM post p
		    LEFT JOIN likes l ON l.post_id = p.id
		    LEFT JOIN scrap s ON s.post_id = p.id
		    LEFT JOIN post_image pi ON pi.post_id = p.id
		    WHERE p.id = :postId
		    GROUP BY p.id 
		""", nativeQuery = true)
	Optional<PostResponseDTO> findPostDetailByIdSorted(@Param("postId") Long sortBy);

}

