package com.project2.domain.post.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.project2.domain.post.entity.Post;

public interface PostRepository extends JpaRepository<Post, Long> {

	@Query("""
		    SELECT p, COUNT(l.id) as likeCount
		    FROM Post p
		    LEFT JOIN Likes l ON l.post.id = p.id
		    GROUP BY p.id
		    ORDER BY likeCount DESC, p.createdDate DESC
		""")
	Page<Post> findAllSortedByLike(Pageable pageable);

	@Query("""
		    SELECT p, COUNT(s.id) as scrapCount
		    FROM Post p
		    LEFT JOIN Scrap s ON s.post.id = p.id
		    GROUP BY p.id
		    ORDER BY scrapCount DESC, p.createdDate DESC
		""")
	Page<Post> findAllSortedByScrap(Pageable pageable);

	Page<Post> findAllByOrderByCreatedDateDesc(Pageable pageable);

}

