package com.project2.domain.member.controller;

import com.project2.domain.member.dto.MemberDTO;
import com.project2.domain.member.entity.Member;
import com.project2.domain.member.service.MemberService;
import com.project2.global.dto.Empty;
import com.project2.global.dto.RsData;
import com.project2.global.security.Rq;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "MemberController", description = "회원 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/members")
public class MemberController {

    private final MemberService memberService;
    private final Rq rq;

    @Operation(summary = "내 정보 조회")
    @GetMapping("/me")
    public RsData<MemberDTO> me() {

        Member actor = rq.getActor();
        Member realActor = rq.getRealActor(actor);

        return new RsData<>(
                "200",
                "내 정보 조회가 완료되었습니다.",
                new MemberDTO(realActor)
        );
    }

    @Operation(summary = "로그아웃", description = "로그아웃 시 쿠키 삭제")
    @DeleteMapping("/logout")
    public RsData<Empty> logout(HttpSession session) {

        session.invalidate();
        SecurityContextHolder.clearContext();
        rq.removeCookie("accessToken");
        rq.removeCookie("refreshToken");

        return new RsData<>("200", "로그아웃 되었습니다.");
    }
}
