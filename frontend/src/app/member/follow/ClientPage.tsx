"use client";

import client from "@/lib/backend/client"; // 필요에 따라 수정
import { useRouter } from "next/navigation";
import { useState } from "react";

export default function ClientPage() {
    const router = useRouter();
    const [isFollowing, setIsFollowing] = useState(false);

    const handleFollow = async () => {
        // 팔로우 API 호출
        await client.post("/api/follow"); // API 엔드포인트는 필요에 따라 수정
        setIsFollowing(true);
    };

    const handleUnfollow = async () => {
        // 언팔로우 API 호출
        await client.delete("/api/follow"); // API 엔드포인트는 필요에 따라 수정
        setIsFollowing(false);
    };

    return (
        <div className="flex justify-center items-center min-h-screen bg-gray-50">
            <div className="w-full max-w-md p-8 bg-white border border-gray-200 rounded shadow-sm">
                <h2 className="text-center mb-6 text-xl font-medium">
                    팔로우/언팔로우 기능
                </h2>

                {/* Follow/Unfollow Button */}
                <button
                    className={`w-full py-3 ${
                        isFollowing ? "bg-red-500" : "bg-green-500"
                    } text-white font-medium rounded flex items-center justify-center`}
                    onClick={isFollowing ? handleUnfollow : handleFollow}
                >
                    {isFollowing ? "언팔로우" : "팔로우"}
                </button>
            </div>
        </div>
    );
}
