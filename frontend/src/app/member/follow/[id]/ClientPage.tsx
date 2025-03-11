"use client";

import { components } from "@/lib/backend/schema";
import { useState } from "react";
import { client } from "@/lib/backend/client";


interface ClientPageProps {
    memberId: number;
    followingList?:  RsData<List<components["schemas"]["FollowResponseDto"]>>;
    followerList?: RsData<List<components["schemas"]["FollowResponseDto"]>>;
    allMembers: components["schemas"]["MemberDTO"][];
    totalPages: number;
    profileData: components["schemas"]["MemberProfileRequestDTO"]
}

export default function ClientPage({
                                       memberId,
                                       followingList,
                                       followerList,
                                       allMembers,
                                       totalPages,

                                   }: ClientPageProps) {
    const [activeTab, setActiveTab] = useState("팔로잉");
    const [followingListState, setFollowingList] = useState(followingList);
    const [followerListState, setFollowerList] = useState(followerList);
    const [searchTerm, setSearchTerm] = useState(""); // 검색어 상태 추가

    const handleFollowToggle = async (userId: number) => {
        try {
            const requestBody: components["schemas"]["FollowRequestDto"] = {
                followerId: memberId,
                followingId: userId,
            };

            await client.POST("/api/follows/{memberId}/follows", {
                method: "POST", // POST 메서드 명시
                body: JSON.stringify(requestBody), // requestBody를 JSON 문자열로 변환
                headers: {
                    "Content-Type": "application/json", // Content-Type 헤더 추가
                },
                params: {
                    path: {
                        memberId: memberId,
                    },
                },
            });
            // 팔로잉 및 팔로워 목록 업데이트
            const followingResponse = await client.GET("/api/follows/{memberId}/followings", {
                params: {
                    path: {
                        memberId: memberId, // memberId 타입을 명시적으로 지정
                    },
                },
            });

            const followerResponse = await client.GET(
                "/api/follows/{memberId}/followers", {
                    params: {
                        path: {
                            memberId: memberId, // memberId 타입을 명시적으로 지정
                        },
                    },
                });


            setFollowingList(followingResponse.data.data);
            setFollowerList(followerResponse.data.data);

        } catch (error) {
            console.error("팔로우/언팔로우 실패:", error);
        }
    };

    const isFollowing = (userId: number) => {
        return followingListState?.some((user) => user.followingId === userId);
    };

    // 검색 기능이 적용된 멤버 목록
    const filteredMembers = allMembers.filter(member =>
        member.nickname.toLowerCase().includes(searchTerm.toLowerCase())
    );

    return (
        <div className="flex flex-col items-center w-full min-h-screen bg-white p-6">
            <div className="flex justify-between mb-4 mt-8 w-full max-w-2xl">
                <button
                    className={`px-4 py-2 rounded ${
                        activeTab === "팔로워" ? "bg-blue-500 text-white" : "bg-gray-200"
                    }`}
                    onClick={() => setActiveTab("팔로워")}
                >
                    팔로워
                </button>
                <button
                    className={`px-4 py-2 rounded ${
                        activeTab === "팔로잉" ? "bg-blue-500 text-white" : "bg-gray-200"
                    }`}
                    onClick={() => setActiveTab("팔로잉")}
                >
                    팔로잉
                </button>
                <div className="flex items-center">
                    <input
                        type="text"
                        placeholder="사용자 검색"
                        className="border border-gray-300 rounded p-2 mr-2"
                        value={searchTerm}
                        onChange={(e) => setSearchTerm(e.target.value)}
                    />
                    <button className="px-4 py-2 bg-gray-500 text-white rounded">
                        검색
                    </button>
                </div>
            </div>

            {activeTab === "팔로잉" ? (
                followingListState && followingListState.length > 0 ? (
                    followingListState.map((follow) => {
                        const following = allMembers.find(member => member.id === follow.followingId);
                        return (
                            <div
                                key={follow.followingId}
                                className="flex items-center justify-between border-b py-2 w-full max-w-2xl"
                            >
                                <div className="flex items-center">
                                    <div className="w-8 h-8 rounded-full bg-gray-300 mr-2"></div>
                                    <span>{following?.nickname || "알 수 없는 사용자"}</span>
                                </div>
                                <button
                                    className={`px-4 py-2 rounded bg-red-500 text-white`}
                                    onClick={() => {
                                        if (follow.followingId !== undefined) {
                                            handleFollowToggle(follow.followingId);
                                        }
                                    }}
                                >
                                    언팔로우
                                </button>
                            </div>
                        );
                    })
                ) : (
                    <div className="w-full max-w-2xl">팔로잉 목록이 없습니다.</div>
                )
            ) : (
                followerListState && followerListState.length > 0 ? (
                    followerListState.map((follow) => {
                        const follower = allMembers.find(member => member.id === follow.followerId);
                        return (
                            <div
                                key={follow.followerId}
                                className="flex items-center justify-between border-b py-2 w-full max-w-2xl"
                            >
                                <div className="flex items-center">
                                    <div className="w-8 h-8 rounded-full bg-gray-300 mr-2"></div>
                                    <span>{follower?.nickname || "알 수 없는 사용자"}</span>
                                </div>
                                <button
                                    className={`px-4 py-2 rounded ${
                                        follow.followerId !== undefined && isFollowing(follow.followerId) ? "bg-red-500" : "bg-green-500"
                                    } text-white`}
                                    onClick={() => {
                                        if (follow.followerId !== undefined) {
                                            handleFollowToggle(follow.followerId);
                                        }
                                    }}
                                >
                                    {follow.followerId !== undefined && isFollowing(follow.followerId) ? "언팔로우" : "팔로우"}
                                </button>
                            </div>
                        );
                    })
                ) : (
                    <div className="w-full max-w-2xl">팔로워 목록이 없습니다.</div>
                )
            )}

            {/* 검색 결과 표시 - 검색어가 있을 때만 표시 */}
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
                                    <div className="w-8 h-8 rounded-full bg-gray-300 mr-2"></div>
                                    <span>{member.nickname}</span>
                                </div>
                                <button
                                    className={`px-4 py-2 rounded ${
                                        isFollowing(member.id) ? "bg-red-500" : "bg-green-500"
                                    } text-white`}
                                    onClick={() => handleFollowToggle(member.id)}
                                >
                                    {isFollowing(member.id) ? "언팔로우" : "팔로우"}
                                </button>
                            </div>
                        ))
                    ) : (
                        <div>검색 결과가 없습니다.</div>
                    )}
                </div>
            )}

            <div className="flex justify-center mt-4 w-full max-w-2xl">
                {/* 페이지네이션 버튼 */}
                {Array.from({ length: totalPages }, (_, i) => (
                    <button key={i} className="mx-1 px-3 py-1 border rounded">
                        {i + 1}
                    </button>
                ))}
                {totalPages > 1 && (
                    <button className="mx-1 px-3 py-1 border rounded">&gt;</button>
                )}
            </div>
        </div>
    );
}