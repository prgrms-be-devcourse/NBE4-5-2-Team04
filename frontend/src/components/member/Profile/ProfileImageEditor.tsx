import { useProfileImage } from "@/app/member/[memberId]/hooks/useProfileImage";
import ProfileImage from "@/components/ui/ProfileImage";

export default function ProfileImageEditor({
  initialImageUrl,
  memberId,
  isEditable,
  refreshProfile,
}: {
  initialImageUrl: string;
  memberId: number;
  isEditable: boolean;
  refreshProfile: () => void;
}) {
  const { profileImageUrl, selectedFile, handleImageChange, handleSaveImage } =
    useProfileImage(initialImageUrl, memberId, refreshProfile);

  return (
    <div className="relative flex flex-col items-center">
      <ProfileImage
        src={profileImageUrl}
        alt="프로필 이미지"
        width={120}
        height={120}
        className="rounded-full"
      />

      {isEditable && (
        <>
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
        </>
      )}
    </div>
  );
}
