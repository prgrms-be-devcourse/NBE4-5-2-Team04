export default function ProfileStats({ profileData }: { profileData: any }) {
  return (
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
  );
}
