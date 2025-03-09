"use client";

import { components } from "@/lib/backend/schema";
import { useState } from "react";
import Image from "next/image";

export default function ClientPage({
  profileData,
  memberId,
}: {
  profileData: components["schemas"]["MemberProfileRequestDTO"];
  memberId: number;
}) {
  const [imageSrc, setImageSrc] = useState(
    profileData.profileImageUrl || "/default-profile.png"
  );

  const [nickname, setNickname] = useState(profileData.nickname);
  const [isEditing, setIsEditing] = useState(false);

  const handleEditNickname = () => {
    setIsEditing(true);
  };

  const handleSaveNickname = () => {
    // TODO: 닉네임 변경 API 호출
    setIsEditing(false);
  };

  // const response = await client.GET("/api/posts/member/{memberId}", {
  //   params: {
  //     query: {
  //       pageable: {
  //         page: 1,
  //         size: 1,
  //         sort: ["createdAt"],
  //       },
  //     },
  //     path: {
  //       memberId: memberId,
  //     },
  //   },
  //   credentials: "include",
  // });

  return (
    <div className="flex flex-col items-center w-full min-h-screen bg-white p-6">
      {/* 프로필 컨테이너 - 가로폭 확장 */}
      <div className="flex flex-col items-center w-full min-h-screen bg-white p-6">
        {/* 프로필 사진 및 수정 버튼 */}
        <div className="relative flex flex-col items-center">
          <Image
            src={profileData.profileImageUrl}
            alt="프로필 이미지"
            width={120}
            height={120}
            className="rounded-full"
          />
          <button className="mt-3 px-4 py-2 bg-black text-white text-sm font-bold rounded">
            프로필 파일 수정
          </button>
        </div>

        {/* 닉네임 & 수정 버튼 */}
        <div className="flex items-center justify-center gap-2 mt-4">
          <h2 className="text-2xl font-bold">{nickname}</h2>
          <button className="px-3 py-1 bg-gray-500 text-white text-sm rounded">
            수정
          </button>
        </div>

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

        {/* 게시글 입력 */}
        <div className="w-full max-w-4xl">
          <div className="flex justify-between mb-2">
            <p className="text-lg font-bold">내 게시글</p>
            <p className="text-lg font-bold">글 작성하기</p>
          </div>
          <input
            type="text"
            placeholder="여행 이야기를 입력해주세요"
            className="w-full border border-gray-300 rounded p-3"
          />
        </div>
      </div>
    </div>
  );
}
