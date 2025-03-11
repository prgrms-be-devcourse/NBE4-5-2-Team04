package com.project2.domain.member.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.project2.domain.member.dto.MemberDTO;
import com.project2.domain.member.dto.MemberProfileRequestDTO;
import com.project2.domain.member.dto.UpdateNicknameDTO;
import com.project2.domain.member.entity.Member;
import com.project2.domain.member.service.AuthService;
import com.project2.domain.member.service.FollowerService;
import com.project2.domain.member.service.FollowingService;
import com.project2.domain.member.service.MemberService;
import com.project2.domain.post.service.PostService;
import com.project2.global.dto.Empty;
import com.project2.global.dto.RsData;
import com.project2.global.security.Rq;
import com.project2.global.util.ImageService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "MemberController", description = "회원 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/members")
public class MemberController {

	private final AuthService authService;
	private final MemberService memberService;
	private final PostService postService;
	private final FollowerService followerService;
	private final FollowingService followingService;
	private final ImageService imageService;
	private final Rq rq;

	@Operation(summary = "내 정보 조회")
	@GetMapping("/me")
	public RsData<MemberDTO> me() {

		Member actor = rq.getActor();
		Member realActor = rq.getRealActor(actor);

		return new RsData<>("200", "내 정보 조회가 완료되었습니다.", new MemberDTO(realActor));
	}

	/**
	 * 로그아웃 API 엔드포인트
	 * 실제 로그아웃 처리는 Spring Security의 LogoutFilter에서 담당합니다.
	 * 이 메서드는 SpringDoc을 통해 프론트엔드에 API 명세를 제공하기 위한 용도입니다.
	 *
	 * @return 로그아웃 성공 응답
	 */
	@Operation(summary = "로그아웃", description = "사용자 로그아웃 처리")
	@DeleteMapping("/logout")
	public RsData<Empty> logout() {
		return new RsData<>("200", "로그아웃이 완료되었습니다.", new Empty());
	}

	@Operation(summary = "리프레시 토큰을 이용한 액세스 토큰 갱신")
	@GetMapping("/refresh")
	public RsData<MemberDTO> refreshAccessToken() {
		String refreshToken = rq.getValueFromCookie("refreshToken");
		if (refreshToken == null || refreshToken.isEmpty()) {
			return new RsData<>("401", "리프레시 토큰이 제공되지 않았습니다.");
		}

		Member actor = authService.getMemberByRefreshTokenOrThrow(refreshToken);
		Member realActor = rq.getRealActor(actor);
		String newAccessToken = authService.genAccessToken(realActor);
		rq.addCookie("accessToken", newAccessToken, false);

		return new RsData<>("200", "액세스 토큰이 갱신되었습니다.", new MemberDTO(realActor));
	}

	@Operation(summary = "사용자 정보 프로필을 조회합니다.")
	@GetMapping("/{memberId}")
	public RsData<MemberProfileRequestDTO> getMemberProfile(@PathVariable long memberId) {
		Member member = this.memberService.findByIdOrThrow(memberId);
		//long totalPostCount = this.postService.getPostById(memberId, pageable).getTotalElements();
		long totalFollowersCount = this.followerService.getFollowersCount(memberId);
		long totalFollowingsService = this.followingService.getFollowingsCount(memberId);
		return new RsData<>("200", "사용자 프로필 조회가 완료되었습니다.",
			new MemberProfileRequestDTO(member, 0, totalFollowersCount, totalFollowingsService));
	}

	@Operation(summary = "전체 회원 조회")
	@GetMapping("/totalMember")
	public RsData<List<MemberDTO>> getAllMembers() {
		List<Member> members = memberService.findAllMembers(); // 모든 회원을 조회하는 메서드 호출
		List<MemberDTO> memberDTOs = members.stream()
			.map(MemberDTO::new) // Member 객체를 MemberDTO로 변환
			.collect(Collectors.toList());

		return new RsData<>(
			"200",
			"전체 회원 조회가 완료되었습니다.",
			memberDTOs
		);
	}

	@Operation(summary = "사용자 정보 프로필 이미지를 수정합니다.")
	@PutMapping(value = "/profile-image/{memberId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public RsData<MemberProfileRequestDTO> updateProfileImage(
		@PathVariable long memberId,
		@RequestParam("profileImage") MultipartFile profileImage) {  // ✅ @RequestParam을 사용하여 파일을 받음

		// 파일 저장
		String savedImagePath = imageService.storeProfileImage(memberId, profileImage);

		// DB에 저장
		Member updatedMember = memberService.updateProfileImageUrl(memberId, savedImagePath);

		return getMemberProfile(updatedMember.getId());
	}

	@Operation(summary = "사용자 닉네임을 수정합니다.")
	@PutMapping("/nickname/{memberId}")
	public RsData<MemberProfileRequestDTO> updateNickname(@PathVariable long memberId,
		@RequestBody UpdateNicknameDTO updateNicknameDTO) {
		Member updatedMember = memberService.updateNickname(memberId, updateNicknameDTO.getNickname());
		return getMemberProfile(updatedMember.getId());
	}
}
