package com.project2.domain.member.service;

import com.project2.domain.member.entity.Member;
import com.project2.domain.member.enums.Provider;
import com.project2.domain.member.repository.MemberRepository;
import com.project2.global.exception.ServiceException;
import com.project2.global.util.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class MemberService {

    private final MemberRepository memberRepository;
    private final ImageService imageService;

    @Transactional
    public Member signUp(String email, String nickname, String profileImage, Provider provider) {

        Member member = memberRepository.save(Member.builder()
                .email(email)
                .nickname(nickname)
                .provider(provider)
                .profileImageUrl("")
                .build());

        Long memberId = member.getId();

        String profileImagePath = imageService.downloadProfileImage(profileImage, memberId);

        member.setProfileImageUrl(profileImagePath);

        return member;
    }

    @Transactional
    public Optional<Member> findById(Long id) {
        return memberRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Member findByIdOrThrow(Long id) {
        return memberRepository.findById(id).orElseThrow(() ->
                new ServiceException("404", "사용자를 찾을 수 없습니다."));
    }

    public Optional<Member> findByEmail(String email) {
        return memberRepository.findByEmail(email);
    }
}
