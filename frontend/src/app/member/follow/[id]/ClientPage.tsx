"use client";

import { components } from "@/lib/backend/schema";
import { useState, useEffect } from "react";
import { useSearchParams } from "next/navigation";
import { client } from "@/lib/backend/client";
import ProfileImage from "@/components/ui/ProfileImage";
import { Button } from "@/components/ui/button";

interface ClientPageProps {
  memberId: number;
  followingList?: components["schemas"]["FollowerResponseDto"][];
  followerList?: components["schemas"]["FollowerResponseDto"][];
  allMembers: components["schemas"]["MemberDTO"][];
  totalPages: number;
}

export default function ClientPage({
  memberId,
  followingList,
  followerList,
  allMembers,
  totalPages,
}: ClientPageProps) {
  const searchParams = useSearchParams();
  const tabFromURL = searchParams.get("tab") || "following";
  const [activeTab, setActiveTab] = useState(tabFromURL);

  const [followingListState, setFollowingList] = useState(followingList);
  const [followerListState, setFollowerList] = useState(followerList);
  const [searchTerm, setSearchTerm] = useState("");

  useEffect(() => {
    setActiveTab(tabFromURL);
  }, []);

  const handleTabChange = (tab: "follower" | "following") => {
    setActiveTab(tab);
  };

  const handleFollowToggle = async (userId: number) => {
    try {
      const requestBody: components["schemas"]["FollowRequestDto"] = {
        followerId: memberId,
        followingId: userId,
      };

      await client.POST("/api/follows/{memberid}/follows", {
        body: requestBody,
        headers: {
          "Content-Type": "application/json",
        },
        params: { path: { memberid: memberId } },
        credentials: "include",
      });

      const followingResponse = await client.GET(
        "/api/follows/{memberId}/followings",
        {
          params: { path: { memberId } },
          credentials: "include",
        }
      );

      const followerResponse = await client.GET(
        "/api/follows/{memberId}/followers",
        {
          params: { path: { memberId } },
          credentials: "include",
        }
      );

      setFollowingList(followingResponse?.data?.data ?? []);
      setFollowerList(followerResponse?.data?.data ?? []);
    } catch (error) {
      console.error("팔로우/언팔로우 실패:", error);
    }
  };

  const isFollowing = (userId: number) => {
    return followingListState?.some((user) => user.userId === userId);
  };

  const filteredFollowerList = followerListState?.filter(
    (follower) => follower.userId !== memberId
  );
  const filteredMembers = allMembers.filter((member) =>
    member.nickname.toLowerCase().includes(searchTerm.toLowerCase())
  );

  return (
    <div className="flex flex-col items-center w-full min-h-screen bg-white p-6">
      <div className="flex justify-between mb-4 mt-8 w-full max-w-2xl">
        <Button
          className={`px-4 py-2 rounded ${
            activeTab === "follower" ? "bg-blue-500 text-white" : "bg-gray-200"
          }`}
          onClick={() => handleTabChange("follower")}
        >
          팔로워
        </Button>
        <Button
          className={`px-4 py-2 rounded ${
            activeTab === "following" ? "bg-blue-500 text-white" : "bg-gray-200"
          }`}
          onClick={() => handleTabChange("following")}
        >
          팔로잉
        </Button>
      </div>

      {activeTab === "following" ? (
        allMembers
          .filter((member) => member.id !== memberId)
          .map((member) => (
            <div
              key={member.id}
              className="flex items-center justify-between border-b py-2 w-full max-w-2xl"
            >
              <div className="flex items-center">
                <ProfileImage
                  src={member.profileImageUrl || "/default-profile.png"}
                  alt="프로필 이미지"
                  width={32}
                  height={32}
                  className="rounded-full mr-2"
                />
                <span>{member.nickname}</span>
              </div>
              {isFollowing(member.id) ? (
                <Button
                  className="px-4 py-2 rounded bg-red-500 text-white"
                  onClick={() => handleFollowToggle(member.id)}
                >
                  언팔로우
                </Button>
              ) : (
                <Button
                  className="px-4 py-2 rounded bg-blue-500 text-white"
                  onClick={() => handleFollowToggle(member.id)}
                >
                  팔로우
                </Button>
              )}
            </div>
          ))
      ) : filteredFollowerList && filteredFollowerList.length > 0 ? (
        filteredFollowerList.map((follow) => {
          const follower = allMembers.find(
            (member) => member.id === follow.userId
          );
          return (
            <div
              key={follow.userId}
              className="flex items-center border-b py-2 w-full max-w-2xl"
            >
              <div className="flex items-center">
                <ProfileImage
                  src={follow.profileImageUrl || "/default-profile.png"}
                  alt="프로필 이미지"
                  width={32}
                  height={32}
                  className="rounded-full mr-2"
                />
                <span>{follower?.nickname || "알 수 없는 사용자"}</span>
              </div>
            </div>
          );
        })
      ) : (
        <div className="w-full max-w-2xl">팔로워 목록이 없습니다.</div>
      )}
    </div>
  );
}
