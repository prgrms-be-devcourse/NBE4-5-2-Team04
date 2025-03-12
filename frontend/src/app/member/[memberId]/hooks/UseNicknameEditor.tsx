import { useState } from "react";
import { client } from "@/lib/backend/client";

export function useNicknameEditor(
  initialNickname: string,
  memberId: number,
  refreshProfile: () => void
) {
  const [nickname, setNickname] = useState(initialNickname);
  const [isEditing, setIsEditing] = useState(false);

  const handleEdit = () => setIsEditing(true);

  const handleSave = async () => {
    if (!nickname.trim()) {
      alert("닉네임은 비어 있을 수 없습니다.");
      return;
    }

    try {
      const response = await client.PUT("/api/members/nickname/{memberId}", {
        params: { path: { memberId } },
        credentials: "include",
        body: { nickname },
      });

      if (response.error) {
        alert(response.error.msg);
        return;
      }

      if (response.data) {
        setIsEditing(false);
        alert("닉네임이 변경되었습니다.");
        refreshProfile(); // 프로필 데이터 갱신!
      } else {
        alert("닉네임 변경 실패");
      }
    } catch (error) {
      console.log(error);
      alert("닉네임 변경 중 오류 발생");
    }
  };

  return {
    nickname,
    isEditing,
    setNickname,
    handleEdit,
    handleSave,
  };
}
