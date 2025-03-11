"use client";

import { components } from "@/lib/backend/schema";
import { useState } from "react";
import { client } from "@/lib/backend/client";
import ProfileImage from "@/components/ui/ProfileImage";

export default function ClientPage({
  profileData,
  // postData,
  memberId,
}: {
  profileData: components["schemas"]["MemberProfileRequestDTO"];
  // postData: components["schemas"]["MEMBER"];
  memberId: number;
}) {
  const [profileImageUrl, setProfileImageUrl] = useState(
    profileData.profileImageUrl || "/default-profile.png"
  );
  const [nickname, setNickname] = useState(profileData.nickname);
  const [isEditing, setIsEditing] = useState(false);
  const [selectedFile, setSelectedFile] = useState<File | null>(null);

  /** 🔹 프로필 이미지 변경 */
  const handleImageChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    const file = event.target.files?.[0];
    if (file) {
      setSelectedFile(file);
      const url = URL.createObjectURL(file);
      console.log(url);
      setProfileImageUrl(url); // 미리보기
    }
  };

  const handleSaveImage = async () => {
    if (!selectedFile) return;

    const formData = new FormData();
    formData.append("profileImage", selectedFile);

    try {
      console.log(
        "전송 파일:",
        selectedFile.name,
        selectedFile.type,
        selectedFile.size
      );
      console.log("회원 ID:", memberId);

      const response = await fetch(
        `http://localhost:8080/api/members/profile-image/${memberId}`,
        {
          method: "PUT",
          body: formData,
          credentials: "include",
        }
      );

      console.log("응답 상태:", response.status);
      const responseText = await response.text();
      console.log("응답 본문:", responseText);

      if (!response.ok) {
        throw new Error(
          `이미지 업로드 실패 (${response.status}): ${responseText}`
        );
      }

      // JSON인 경우에만 파싱
      let data;
      try {
        data = JSON.parse(responseText);
        if (data.data && data.data.profileImageUrl) {
          const newProfileImageUrl = data.data.profileImageUrl;

          // 이미지 URL 강제 업데이트 (캐시 문제 해결)
          setProfileImageUrl(`${newProfileImageUrl}?t=${new Date().getTime()}`);

          // 선택된 파일 초기화 (저장 버튼 숨기기)
          setSelectedFile(null);

          alert("프로필 이미지가 변경되었습니다.");
        } else {
          alert("서버에서 올바른 응답을 받지 못했습니다.");
        }
      } catch (e) {
        console.error("JSON 응답 파싱 오류:", e);
        alert("서버 응답 처리 중 오류 발생");
      }
    } catch (error) {
      console.error("이미지 업로드 오류:", error);
      alert("이미지 업로드 중 오류 발생");
    }
  };

  /** 🔹 닉네임 수정 */
  const handleEditNickname = () => setIsEditing(true);
  const handleSaveNickname = async () => {
    try {
      console.log(nickname);

      if (nickname == undefined) {
        alert("닉네임은 비어 있을 수 없습니다.");
        return;
      }

      const response = await client.PUT("/api/members/nickname/{memberId}", {
        params: {
          path: { memberId },
        },
        credentials: "include",
        body: {
          nickname: nickname,
        },
      });

      if (response.data) {
        setIsEditing(false);
        alert("닉네임이 변경되었습니다.");
      } else {
        alert("닉네임 변경 실패");
      }
    } catch (error) {
      console.error("Error updating nickname:", error);
      alert("닉네임 변경 중 오류 발생");
    }
  };

  return (
    <div className="flex flex-col items-center w-full min-h-screen bg-white p-6">
      {/* 프로필 컨테이너 - 기존 UI 유지 */}
      <div className="flex flex-col items-center w-full min-h-screen bg-white p-6">
        {/* 프로필 사진 및 수정 버튼 */}
        <div className="relative flex flex-col items-center">
          <ProfileImage
            src={profileImageUrl}
            alt="프로필 이미지"
            width={120}
            height={120}
            className="rounded-full"
          />
          <input
            type="file"
            accept="image/*"
            onChange={handleImageChange}
            className="hidden"
            id="imageUpload"
          />
          <label
            htmlFor="imageUpload"
            className="mt-3 px-4 py-2 bg-black text-white text-sm font-bold rounded cursor-pointer"
          >
            프로필 변경
          </label>
          {selectedFile && (
            <button
              onClick={handleSaveImage}
              className="mt-2 px-4 py-2 bg-blue-500 text-white text-sm font-bold rounded"
            >
              저장
            </button>
          )}
        </div>

        {/* 닉네임 & 수정 버튼 */}
        <div className="flex items-center justify-center gap-2 mt-4">
          {isEditing ? (
            <input
              type="text"
              value={nickname}
              onChange={(e) => setNickname(e.target.value)}
              className="border border-gray-300 rounded p-2"
            />
          ) : (
            <h2 className="text-2xl font-bold">{nickname}</h2>
          )}
          {isEditing ? (
            <button
              onClick={handleSaveNickname}
              className="px-3 py-1 bg-blue-500 text-white text-sm rounded"
            >
              저장
            </button>
          ) : (
            <button
              onClick={handleEditNickname}
              className="px-3 py-1 bg-gray-500 text-white text-sm rounded"
            >
              수정
            </button>
          )}
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
