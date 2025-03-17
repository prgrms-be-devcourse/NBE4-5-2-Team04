package com.project2.domain.member.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.project2.domain.member.entity.Follows;
import com.project2.domain.member.entity.Member;

@Repository
public interface FollowRepository extends JpaRepository<Follows, Long> {
	Optional<Follows> findByFollowerAndFollowing(Member follower, Member following);

	Page<Follows> findByFollowing(Member following, Pageable pageable);

	List<Follows> findByFollowing(Member following);

	List<Follows> findByFollower(Member follower);

	long countByFollower(Member follower);

	long countByFollowing(Member member);

	boolean existsByFollowerAndFollowing(Member follower, Member following);

	void deleteByFollowerAndFollowing(Member follower, Member following);
}