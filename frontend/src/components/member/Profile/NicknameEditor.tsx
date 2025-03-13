import { useNicknameEditor } from "@/app/member/[memberId]/hooks/UseNicknameEditor";

export default function NicknameEditor({
  initialNickname,
  memberId,
  isEditable,
  refreshProfile, // ✅ 추가
}: {
  initialNickname: string;
  memberId: number;
  isEditable: boolean;
  refreshProfile: () => void; // ✅ 함수 타입 추가
}) {
  const { nickname, isEditing, setNickname, handleEdit, handleSave } =
    useNicknameEditor(initialNickname, memberId, refreshProfile);

  return (
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
      {isEditable &&
        (isEditing ? (
          <button
            onClick={handleSave}
            className="px-3 py-1 bg-blue-500 text-white text-sm rounded"
          >
            저장
          </button>
        ) : (
          <button
            onClick={handleEdit}
            className="px-3 py-1 bg-gray-500 text-white text-sm rounded"
          >
            수정
          </button>
        ))}
    </div>
  );
}
