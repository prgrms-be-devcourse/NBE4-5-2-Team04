package com.project2.domain.member.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.project2.domain.member.entity.Member;

public interface MemberRepository extends JpaRepository<Member, Long> {
}
