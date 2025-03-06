package com.project2.domain.member.service;

import com.project2.domain.member.entity.Member;
import com.project2.domain.member.enums.Provider;
import com.project2.domain.member.repository.MemberRepository;
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
        String profileImagePath = imageService.downloadProfileImage(profileImage);

        Member member = Member.builder()
                .email(email)
                .nickname(nickname)
                .provider(provider)
                .profileImageUrl(profileImagePath)
                .build();

        return memberRepository.save(member);
    }

    @Transactional
    public Optional<Member> findById(Long id) {
        return memberRepository.findById(id);
    }

    public Optional<Member> findByEmail(String email) {
        return memberRepository.findByEmail(email);
    }
}
