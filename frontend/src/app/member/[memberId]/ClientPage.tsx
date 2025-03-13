"use client";

import { components } from "@/lib/backend/schema";
import NicknameEditor from "@/components/member/Profile/NicknameEditor";
import ProfileImageEditor from "@/components/member/Profile/ProfileImageEditor";
import Link from "next/link"; // Next.js Link 컴포넌트 사용

export default function ClientPage({
                                     profileData,
                                     memberId,
                                     refreshProfile,
                                   }: {
  profileData: components["schemas"]["MemberProfileResponseDTO"];
  memberId: number;
  refreshProfile: () => void;
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
              refreshProfile={refreshProfile}
          />

          {/* 닉네임 수정 (refreshProfile 전달) */}
          <NicknameEditor
              initialNickname={profileData.nickname ?? ""}
              memberId={memberId}
              isEditable={profileData.me ?? false}
              refreshProfile={refreshProfile}
          />

          {/* 통계 정보 */}
          <div className="flex justify-center gap-8 mt-6">
            <div className="text-center">
              <p className="text-lg font-bold">{profileData.totalPostCount}</p>
              <p className="text-sm text-gray-600">게시글</p>
            </div>
            <div className="text-center">
              <Link href={`/member/follow/${memberId}`}>
                {/* 팔로워 링크 */}
                <p className="text-lg font-bold">{profileData.totalFlowerCount}</p>
                <p className="text-sm text-gray-600">팔로워</p>
              </Link>
            </div>
            <div className="text-center">
              <Link href={`/member/follow/${memberId}`}>
                {/* 팔로잉 링크 */}
                <p className="text-lg font-bold">{profileData.totalFlowingCount}</p>
                <p className="text-sm text-gray-600">팔로잉</p>
              </Link>
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