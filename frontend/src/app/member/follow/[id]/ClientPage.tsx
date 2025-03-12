"use client";

import { components } from "@/lib/backend/schema";
import { useState } from "react";
import { client } from "@/lib/backend/client";
import ProfileImage from "@/components/ui/ProfileImage";
import { Button } from "@/components/ui/button";

interface ClientPageProps {
    memberId: number;
    followingList?: components["schemas"]["FollowerResponseDto"][];
    followerList?: components["schemas"]["FollowerResponseDto"][];
    allMembers: components["schemas"]["MemberDTO"][];
    totalPages: number;
    // profileData: components["schemas"]["MemberProfileRequestDTO"];
}

export default function ClientPage({
                                       memberId,
                                       followingList,
                                       followerList,
                                       allMembers,
                                       totalPages,
                                       // profileData,
                                   }: ClientPageProps) {
    const [activeTab, setActiveTab] = useState("팔로잉");
    const [followingListState, setFollowingList] = useState(followingList);
    const [followerListState, setFollowerList] = useState(followerList);
    const [searchTerm, setSearchTerm] = useState("");

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
                params: {
                    path: {
                        memberid: memberId,
                    },
                },
                credentials: 'include'
            });

            const followingResponse = await client.GET(
                "/api/follows/{memberId}/followings",
                {
                    params: {
                        path: {
                            memberId: memberId,
                        },
                    },

                    credentials: 'include'
                }
            );

            const followerResponse = await client.GET(
                "/api/follows/{memberId}/followers",
                {
                    params: {
                        path: {
                            memberId: memberId,
                        },
                    },

                    credentials: 'include'
                }
            );
            setFollowingList(followingResponse?.data?.data ?? []);
            setFollowerList(followerResponse?.data?.data ?? []);
            // setFollowingList(followingResponse.data.data);
            // setFollowerList(followerResponse.data.data);
        } catch (error) {
            console.error("팔로우/언팔로우 실패:", error);
        }
    };

    const isFollowing = (userId: number) => {
        return followingListState?.some((user) => user.userId === userId);
    };

    const filteredMembers = allMembers.filter((member) =>
        member.nickname.toLowerCase().includes(searchTerm.toLowerCase())
    );

    const filteredFollowingMembers = allMembers.filter(
        (member) => member.id !== memberId
    );

    const filteredFollowerList = followerListState?.filter(
        (follower) => follower.userId !== memberId
    );

    return (
        <div className="flex flex-col items-center w-full min-h-screen bg-white p-6">
            <div className="flex justify-between mb-4 mt-8 w-full max-w-2xl">
                <Button
                    className={`px-4 py-2 rounded ${
                        activeTab === "팔로워" ? "bg-blue-500 text-white" : "bg-gray-200"
                    }`}
                    onClick={() => setActiveTab("팔로워")}
                >
                    팔로워
                </Button>
                <Button
                    className={`px-4 py-2 rounded ${
                        activeTab === "팔로잉" ? "bg-blue-500 text-white" : "bg-gray-200"
                    }`}
                    onClick={() => setActiveTab("팔로잉")}
                >
                    팔로잉
                </Button>
                <div className="flex items-center">
                    <input
                        type="text"
                        placeholder="사용자 검색"
                        className="border border-gray-300 rounded p-2 mr-2"
                        value={searchTerm}
                        onChange={(e) => setSearchTerm(e.target.value)}
                    />
                    <Button className="px-4 py-2 bg-gray-500 text-white rounded">
                        검색
                    </Button>
                </div>
            </div>

            {activeTab === "팔로잉"
                ? allMembers.filter((member) => member.id !== memberId).map((member) => (
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
                                className={`px-4 py-2 rounded bg-red-500 text-white`}
                                onClick={() => handleFollowToggle(member.id)}
                            >
                                언팔로우
                            </Button>
                        ) : (
                            <Button
                                className={`px-4 py-2 rounded bg-blue-500 text-white`}
                                onClick={() => handleFollowToggle(member.id)}
                            >
                                팔로우
                            </Button>
                        )}
                    </div>
                ))
                : filteredFollowerList && filteredFollowerList.length > 0
                    ? filteredFollowerList.map((follow) => {
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
                    : <div className="w-full max-w-2xl">팔로워 목록이 없습니다.</div>}

            {searchTerm && (
                <div className="mt-8 w-full max-w-2xl">
                    <h3 className="text-lg font-bold mb-4">검색 결과</h3>
                    {filteredMembers.length > 0 ? (
                        filteredMembers.map((member) => (
                            <div
                                key={member.id}
                                className="flex items-center justify-between border-b py-2"
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
                                <Button
                                    className={`px-4 py-2 rounded ${
                                        isFollowing(member.id)
                                            ? "bg-red-500 text-white"
                                            : "bg-blue-500 text-white"
                                    }`}
                                    onClick={() => handleFollowToggle(member.id)}
                                >
                                    {isFollowing(member.id) ? "언팔로우" : "팔로우"}
                                </Button>
                            </div>
                        ))
                    ) : (
                        <div>검색 결과가 없습니다.</div>
                    )}
                </div>
            )}

            <div className="flex justify-center mt-4 w-full max-w-2xl">
                {Array.from({ length: totalPages }, (_, i) => (
                    <Button key={i} className="mx-1 px-3 py-1 border rounded">
                        {i + 1}
                    </Button>
                ))}
                {totalPages > 1 && (
                    <Button className="mx-1 px-3 py-1 border rounded">&gt;</Button>
                )}
            </div>
        </div>
    );
}