import { useState } from "react";

export function useProfileImage(
  initialImageUrl: string,
  memberId: number,
  refreshProfile: () => void
) {
  const [profileImageUrl, setProfileImageUrl] = useState(initialImageUrl);
  const [selectedFile, setSelectedFile] = useState<File | null>(null);

  const handleImageChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    const file = event.target.files?.[0];
    if (file) {
      setSelectedFile(file);
      const url = URL.createObjectURL(file);
      setProfileImageUrl(url);
    }
  };

  const handleSaveImage = async () => {
    if (!selectedFile) return;

    const formData = new FormData();
    formData.append("profileImage", selectedFile);

    try {
      const response = await fetch(
        `${process.env.NEXT_PUBLIC_BASE_URL}/api/members/profile-image/${memberId}`,
        {
          method: "PUT",
          body: formData,
          credentials: "include",
        }
      );

      const responseJson = await response.json();
      if (!response.ok) {
        throw new Error(responseJson.msg || "이미지 업로드 실패!");
      }

      if (responseJson.data && responseJson.data.profileImageUrl) {
        const newProfileImageUrl = responseJson.data.profileImageUrl;
        setProfileImageUrl(`${newProfileImageUrl}`);
        setSelectedFile(null);
        alert("프로필 이미지가 변경되었습니다.");

        refreshProfile();
      } else {
        alert("서버에서 올바른 응답을 받지 못했습니다.");
      }
    } catch (error) {
      alert(
        `오류 발생: ${
          error instanceof Error ? error.message : "알 수 없는 오류"
        }`
      );
    }
  };

  return {
    profileImageUrl,
    selectedFile,
    handleImageChange,
    handleSaveImage,
  };
}
