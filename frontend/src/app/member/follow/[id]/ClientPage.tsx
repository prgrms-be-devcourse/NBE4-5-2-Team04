"use client";

import client from "@/lib/backend/client";
import { useState, useEffect } from "react";
import { components } from "@/lib/backend/schema";

export default function ClientPage() {
    const [activeTab, setActiveTab] = useState("팔로잉");
    const [followingList, setFollowingList] = useState<components["schemas"]["FollowResponseDto"]>();
    const [followerList, setFollowerList] = useState<components["schemas"]["FollowResponseDto"]>();
    const [currentPage, setCurrentPage] = useState(1);
    const [totalPages, setTotalPages] = useState(1);
    const usersPerPage = 4;
    const [allMembers, setAllMembers] = useState<components["schemas"]["MemberDTO"][]>([]); // 빈 배열로 초기화
    const [isLoading, setIsLoading] = useState(true);

    useEffect(() => {
        const memberId = 1;

        const fetchData = async () => {
            try {
                const response = await client.get(
                    `/api/members/${memberId}/${activeTab === "팔로잉" ? "followings" : "followers"}`,
                    {
                        params: {
                            page: currentPage - 1,
                            size: usersPerPage,
                        },
                    }
                );
                if (activeTab === "팔로잉") {
                    setFollowingList(response.data.data);
                } else {
                    setFollowerList(response.data.data);
                }
                setTotalPages(Math.ceil(response.data.totalElements / usersPerPage));
            } catch (error) {
                console.error(`${activeTab} 목록 가져오기 실패:`, error);
            }
        };

        const fetchAllMembers = async () => {
            setIsLoading(true);
            try {
                const response = await client.get(`/api/members`);
                if (response.data) {
                    setAllMembers(response.data);
                }
            } catch (error) {
                console.error("전체 멤버 목록 가져오기 실패:", error);
            } finally {
                setIsLoading(false);
            }
        };

        fetchData();
        fetchAllMembers();
    }, [currentPage, activeTab, followerList]);

    const handleFollowToggle = async (userId: number) => {
        const memberId = 1;
        const userIndex =
            activeTab === "팔로잉"
                ? followingList.findIndex((user) => user.followingId === userId)
                : followerList.findIndex((user) => user.followerId === userId);
        const updatedList =
            activeTab === "팔로잉" ? [...followingList] : [...followerList];
        const updatedUser = updatedList[userIndex];

        if (updatedUser) {
            try {
                const requestBody: components["schemas"]["FollowRequestDto"] = {
                    followerId: memberId,
                    followingId: userId,
                };

                if (updatedUser.followingId === userId) {
                    await client.post(`/api/members/${memberId}/follows`, requestBody);
                } else {
                    await client.delete(`/api/members/${memberId}/follows/${userId}`);
                }

                if (activeTab === "팔로잉") {
                    setFollowingList(updatedList);
                } else {
                    setFollowerList(updatedList);
                }
            } catch (error) {
                console.error("팔로우/언팔로우 실패:", error);
            }
        }
    };

    return (
        <div className="flex justify-center items-center min-h-screen bg-gray-50">
            <div className="w-full max-w-md p-8 bg-white border border-gray-200 rounded shadow-sm">
                <div className="flex justify-between mb-4">
                    <button
                        className={`px-4 py-2 rounded ${
                            activeTab === "팔로우" ? "bg-blue-500 text-white" : "bg-gray-200"
                        }`}
                        onClick={() => setActiveTab("팔로우")}
                    >
                        팔로우
                    </button>
                    <button
                        className={`px-4 py-2 rounded ${
                            activeTab === "팔로잉" ? "bg-blue-500 text-white" : "bg-gray-200"
                        }`}
                        onClick={() => setActiveTab("팔로잉")}
                    >
                        팔로잉
                    </button>
                </div>
                <div className="flex items-center mb-4">
                    <input
                        type="text"
                        placeholder="사용자 검색"
                        className="border rounded px-4 py-2 w-full mr-2"
                    />
                    <button className="px-4 py-2 bg-gray-200 rounded">검색</button>
                </div>
                {activeTab === "팔로잉" ? (
                    isLoading ? (
                        <div>로딩 중...</div>
                    ) : allMembers && allMembers.length > 0 ? (
                        allMembers.map((member) => {
                            const isFollowing = followingList?.some((user) => user.followingId === member.id);
                            return (
                                <div key={member.id} className="flex items-center justify-between border-b py-2">
                                    <div className="flex items-center">
                                        <div className="w-8 h-8 rounded-full bg-gray-300 mr-2"></div>
                                        <span>{member.nickname}</span>
                                    </div>
                                    <button
                                        className={`px-4 py-2 rounded ${
                                            isFollowing ? "bg-red-500" : "bg-green-500"
                                        } text-white`}
                                        onClick={() => handleFollowToggle(member.id)}
                                    >
                                        {isFollowing ? "언팔로우" : "팔로우"}
                                    </button>
                                </div>
                            );
                        })
                    ) : (
                        <div>팔로잉 목록이 없습니다.</div>
                    )
                ) : followerList?.length > 0 ? (
                    followerList.map((user) => (
                        <div key={user.followerId} className="flex items-center justify-between border-b py-2">
                            <div className="flex items-center">
                                <div className="w-8 h-8 rounded-full bg-gray-300 mr-2"></div>
                                <span>{user.followerId}</span>
                            </div>
                            <button
                                className={`px-4 py-2 rounded ${
                                    user.isFollowing ? "bg-red-500" : "bg-green-500"
                                } text-white`}
                                onClick={() => handleFollowToggle(user.followerId)}
                            >
                                {user.isFollowing ? "언팔로우" : "팔로우"}
                            </button>
                        </div>
                    ))
                ) : (
                    <div>팔로워 목록이 없습니다.</div>
                )}
                <div className="flex justify-center mt-4">
                    {Array.from({ length: totalPages }, (_, i) => i + 1).map((page) => (
                        <button
                            key={page}
                            className={`mx-1 px-3 py-1 rounded ${
                                currentPage === page ? "bg-blue-500 text-white" : "bg-gray-200"
                            }`}
                            onClick={() => setCurrentPage(page)}
                        >
                            {page}
                        </button>
                    ))}
                    {currentPage < totalPages && (
                        <button
                            className="ml-2 px-3 py-1 rounded bg-gray-200"
                            onClick={() => setCurrentPage(currentPage + 1)}
                        >
                            &gt;
                        </button>
                    )}
                </div>
            </div>
        </div>
    );
}