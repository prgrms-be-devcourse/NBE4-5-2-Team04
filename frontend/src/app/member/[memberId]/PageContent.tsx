"use client";

import { useState } from "react";
import { client } from "@/lib/backend/client";
import ClientPage from "./ClientPage";
import { components } from "@/lib/backend/schema";
import PostList from "@/components/posts/results/ClientPostList";
import { QueryClient, useQueryClient } from "@tanstack/react-query";

export default function PageContent({
  initialProfileData,
  memberId,
}: {
  initialProfileData: components["schemas"]["MemberProfileResponseDTO"];
  memberId: number;
}) {
  const [profileData, setProfileData] = useState(initialProfileData);
  const queryClient = useQueryClient();

  const refreshProfile = async () => {
    const response = await client.GET("/api/members/{memberId}", {
      params: { path: { memberId } },
      credentials: "include",
    });

    if (!response.error && response.data) {
      setProfileData(response.data.data);

      queryClient.invalidateQueries({
        queryKey: [
          `likedPosts-${response.data.data.nickname || "unknown"}-${
            response.data.data.profileImageUrl || "default"
          }`,
        ],
      });
    }
  };

  return (
    <>
      {/* refreshProfile을 ClientPage로 전달 */}
      <ClientPage
        profileData={profileData}
        memberId={memberId}
        refreshProfile={refreshProfile}
      />
      <PostList
        queryKey={`likedPosts-${profileData.nickname || "unknown"}-${
          profileData.profileImageUrl || "default"
        }`}
        memberId={memberId.toString()}
        apiEndpoint="/api/posts/member/{memberId}"
      />
    </>
  );
}
