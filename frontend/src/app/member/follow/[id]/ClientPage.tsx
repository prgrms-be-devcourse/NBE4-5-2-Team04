"use client";

import { components } from "@/lib/backend/schema";
import {useState, useEffect, useRef} from "react";
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
    totalElements?:number;
    onepageElements:number;
}

export default function ClientPage({
                                       memberId,
                                       followingList,
                                       followerList,
                                       allMembers,
                                       totalPages,
                                       totalElements,
                                       onepageElements,
                                   }: ClientPageProps) {
    const searchParams = useSearchParams();
    const tabFromURL = searchParams.get("tab") || "following";
    const [activeTab, setActiveTab] = useState(tabFromURL);
    const [currentPage, setCurrentPage] = useState(1);
    const pageSize = onepageElements;
    const [totalPagesState, setTotalPages] = useState(totalPages);

    const [followingListState, setFollowingList] = useState(followingList);
    const [followerListState, setFollowerList] = useState(followerList);

    useEffect(() => {
        setActiveTab(tabFromURL);
    }, []);

    useEffect(() => {
        if (activeTab === "follower") {
            fetchFollowers();
        }
    }, [currentPage,activeTab]);

    const fetchFollowers = async () => {
        try {
            console.log("API 응답: currentpage"+currentPage); // 추가된 콘솔 로그

            const page = currentPage - 1;
            const size = pageSize;

            const url = process.env.NEXT_PUBLIC_BASE_URL + `/api/follows/${memberId}/followers?page=${page}&size=${size}`;
            const response = await fetch(
                url,
                {
                    method: "GET",
                    credentials: "include",
                }
            );

            const responseJson = await response.json();
            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }

            console.log("API 응답:!!!!!!!", responseJson);
            setFollowerList(responseJson?.data?.content ?? []);
            setTotalPages(responseJson?.data?.totalPages ?? 1);
        } catch (error) {
            console.error("팔로워 목록 로딩 중 오류 발생:", error);
        }
    };

    console.log("allMembers @@@@@@@@@@"+allMembers);
    const fetchMembers = async () => {
        try {
            const page = currentPage - 1;
            const size = pageSize;

            const url =
                process.env.NEXT_PUBLIC_BASE_URL +
                `/api/totalMember?page=${page}&size=${size}`;
            const response = await fetch(url, {
                method: "GET",
                credentials: "include",
            });

            const responseJson = await response.json();
            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }

            // setMemberList(responseJson?.data?.content ?? []);
            // setMemberTotalPages(responseJson?.data?.totalPages ?? 1);
        } catch (error) {
            console.error("전체 회원 목록 로딩 중 오류 발생:", error);
        }
    };

    const handleTabChange = (tab: "follower" | "following") => {
        setActiveTab(tab);
        setCurrentPage(1);
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
                    params: {
                        path: { memberId },
                        query: { // query 속성 추가
                            pageable: { // pageable 객체 추가
                                page: currentPage - 1, // 현재 페이지 번호 사용
                                size: pageSize, // 현재 페이지 크기 사용
                                // sort: ["id,asc"], // 예시 정렬 정보 (선택 사항)
                            },
                        },
                    },
                    credentials: "include",
                }
            );

            setFollowingList(followingResponse?.data?.data ?? []);
            setFollowerList(followerResponse?.data?.data.content ?? []);

        } catch (error) {
            console.error("팔로우/언팔로우 실패:", error);
        }
    };

    const isFollowing = (userId: number) => {
        return followingListState?.some((user) => user.userId === userId);
    };

    const handlePageChange = (page: number) => {
        console.log("button clicked");
        setCurrentPage(page);
    };


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
            ) : activeTab === "follower" && followerListState && followerListState.length>0 ? (

                <div>
                    {followerListState.map((follow) => {
                        const follower = allMembers.find(
                            (member) => member.id === follow.userId
                        );

                        console.log("1번 여기선 follower 찍히나"+followerListState);

                        return (
                            <div
                                key={follow.userId}
                                className="flex flex-col space-y-2 mb-4 "
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
                    })}
                    <div className="flex justify-center mt-4">
                        {Array.from({ length: totalPagesState }, (_, i) => i + 1).map((page) => (
                            <Button
                                key={page}
                                className={`mx-1 px-3 py-1 rounded ${
                                    currentPage === page ? "bg-blue-500 text-white" : "bg-gray-200"
                                }`}
                                onClick={() => handlePageChange(page)}
                            >
                                {page}
                            </Button>
                        ))}
                    </div>
                </div>
            ) : (
                <div className="w-full max-w-2xl">팔로워 목록이 없습니다.</div>
            )}
        </div>



    );
}
