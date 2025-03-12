package com.project2.domain.rank.repository;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.project2.domain.place.enums.Region;
import com.project2.domain.post.entity.Post;
import com.project2.domain.rank.dto.PopularPlaceDTO;
import com.project2.domain.rank.dto.RegionRankingDTO;

@Repository
public interface RankingRepository extends JpaRepository<Post, Long> {

	// 전국 및 특정 지역 인기 장소 조회 (좋아요 or 스크랩 기준)
	@Query("""
		    SELECT new com.project2.domain.rank.dto.PopularPlaceDTO(
		        pl.id, pl.name, pl.region,
		        COUNT(DISTINCT l.id),
		        COUNT(DISTINCT s.id),
		        COUNT(p.id)
		    )
		    FROM Post p
		    JOIN p.place pl
		    LEFT JOIN p.likes l ON l.post.id = p.id
		    LEFT JOIN p.scraps s ON s.post.id = p.id
		    WHERE p.createdDate >= :startDate
		      AND (:region IS NULL OR pl.region = :region)
		      AND (:placeName IS NULL OR pl.name LIKE %:placeName%)
		    GROUP BY pl.id, pl.name, pl.region
		    ORDER BY
		      CASE WHEN :sort = 'SCRAPS' THEN COUNT(DISTINCT s.id)
		           WHEN :sort = 'LATEST' THEN MAX(p.createdDate)
		           ELSE COUNT(DISTINCT l.id) END DESC
		""")
	Page<PopularPlaceDTO> findPopularPlaces(
		@Param("startDate") LocalDateTime startDate,
		@Param("region") Region region,
		@Param("placeName") String placeName,
		@Param("sort") String sort,
		Pageable pageable
	);

	// 인기 지역 랭킹 조회 (좋아요 합 기준)
	@Query("""
		    SELECT new com.project2.domain.rank.dto.RegionRankingDTO(
		        pl.region,
		        COUNT(DISTINCT l.id),
		        COUNT(DISTINCT s.id),
		        COUNT(p.id)
		    )
		    FROM Post p
		    JOIN p.place pl
		    LEFT JOIN p.likes l ON l.post.id = p.id
		    LEFT JOIN p.scraps s ON s.post.id = p.id
		    WHERE p.createdDate >= :startDate
		    GROUP BY pl.region
		    ORDER BY COUNT(DISTINCT l.id) DESC
		""")
	Page<RegionRankingDTO> findRegionRankings(
		@Param("startDate") LocalDateTime startDate,
		Pageable pageable
	);

	// 특정 지역의 게시글 조회
	@Query("""
		    SELECT p FROM Post p
		    WHERE p.place.region = :region AND p.createdDate >= :startDate
		    ORDER BY SIZE(p.likes) DESC
		""")
	Page<Post> findPostsByRegion(
		@Param("region") Region region,
		@Param("startDate") LocalDateTime startDate,
		Pageable pageable
	);

	// 특정 장소의 게시글 조회 (좋아요, 스크랩, 최신순)
	@EntityGraph(attributePaths = {"place", "member", "images", "likes", "scraps", "comments"})
	@Query("""
		    SELECT p FROM Post p
		    WHERE p.place.id = :placeId AND p.createdDate >= :startDate
		    ORDER BY
		        CASE 
		            WHEN :sort = 'SCRAPS' THEN SIZE(p.scraps)
		            WHEN :sort = 'LATEST' THEN p.createdDate
		            ELSE SIZE(p.likes) END DESC
		""")
	Page<Post> findPostsByPlace(
		@Param("placeId") Long placeId,
		@Param("startDate") LocalDateTime startDate,
		@Param("sort") String sort,
		Pageable pageable
	);
}