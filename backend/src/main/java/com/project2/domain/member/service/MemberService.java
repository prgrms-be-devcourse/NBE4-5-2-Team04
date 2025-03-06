package com.project2.domain.member.service;

import com.project2.domain.member.entity.Member;
import com.project2.domain.member.enums.Provider;
import com.project2.domain.member.repository.MemberRepository;
import com.project2.global.exception.ServiceException;
import com.project2.global.util.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class MemberService {

    private final MemberRepository memberRepository;
    private final ImageService imageService;
    private final AuthTokenService authTokenService;

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

    public Optional<Member> getMemberByAccessToken(String accessToken) {

        Map<String, Object> payload = authTokenService.getPayload(accessToken);

        if (payload == null) {
                return Optional.empty();
        }

        long id = (long) payload.get("id");
        String email = (String) payload.get("email");

        return Optional.of(
                Member.builder()
                        .id(id)
                        .email(email)
                        .build()
        );
    }

    public Optional<Member> getMemberByRefreshToken(String refreshToken) {
        Map<String, Object> payload = authTokenService.getPayload(refreshToken);

        if (payload == null) {
            return Optional.empty();
        }

        long id = (long) payload.get("id");

        return memberRepository.findById(id);
    }

    public Member getMemberByRefreshTokenOrThrow(String refreshToken) {
        return getMemberByRefreshToken(refreshToken)
                .orElseThrow(() -> new ServiceException("401", "유효하지 않은 리프레시 토큰이거나 회원을 찾을 수 없습니다."));
    }

    public String genAccessToken(Member member) {
        return authTokenService.genAccessToken(member);
    }

    public String genRefreshToken(Member member) {
        return authTokenService.genRefreshToken(member.getId());
    }

    public Optional<Member> findByEmail(String email) {
        return memberRepository.findByEmail(email);
    }
}
