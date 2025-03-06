package com.project2.domain.member.repository;

import com.project2.domain.member.entity.Follows;
import com.project2.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FollowRepository extends JpaRepository<Follows, Long> {
    Optional<Follows> findByFollowerAndFollowing(Member follower, Member following);
}