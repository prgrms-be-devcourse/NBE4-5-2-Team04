"use client";

import { components } from "@/lib/backend/schema";
import NicknameEditor from "@/components/member/Profile/NicknameEditor";
import ProfileImageEditor from "@/components/member/Profile/ProfileImageEditor";

export default function ClientPage({
  profileData,
  memberId,
  refreshProfile, // 추가됨
}: {
  profileData: components["schemas"]["MemberProfileResponseDTO"];
  memberId: number;
  refreshProfile: () => void; // 추가됨
}) {
  return (
    <div className="flex flex-col items-center w-full bg-white p-6">
      <div className="flex flex-col items-center w-full bg-white p-6">
        {/* 프로필 이미지 */}
        <ProfileImageEditor
          initialImageUrl={
            profileData.profileImageUrl ?? "/default-profile.png"
          }
          memberId={memberId}
          isEditable={profileData.me ?? false}
          refreshProfile={refreshProfile} // ✅ refreshProfile 전달
        />

        {/* 닉네임 수정 (refreshProfile 전달) */}
        <NicknameEditor
          initialNickname={profileData.nickname ?? ""}
          memberId={memberId}
          isEditable={profileData.me ?? false}
          refreshProfile={refreshProfile} // 추가됨
        />

        {/* 통계 정보 */}
        <div className="flex justify-center gap-8 mt-6">
          <div className="text-center">
            <p className="text-lg font-bold">{profileData.totalPostCount}</p>
            <p className="text-sm text-gray-600">게시글</p>
          </div>
          <div className="text-center">
            <p className="text-lg font-bold">{profileData.totalFlowerCount}</p>
            <p className="text-sm text-gray-600">팔로워</p>
          </div>
          <div className="text-center">
            <p className="text-lg font-bold">{profileData.totalFlowingCount}</p>
            <p className="text-sm text-gray-600">팔로잉</p>
          </div>
          <div className="text-center">
            <p className="text-lg font-bold">{profileData.createdMonthYear}</p>
            <p className="text-sm text-gray-600">가입일</p>
          </div>
        </div>

        {/* 구분선 */}
        <hr className="w-full my-6 border-gray-300" />
      </div>
    </div>
  );
}
