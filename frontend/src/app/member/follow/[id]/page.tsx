import { client } from "@/lib/backend/client";
import ClientPage from "./ClientPage";
import { cookies } from "next/headers";

export default async function Page({
  params,
}: {
  params: {
    id: string;
  };
}) {
  const { id: idString } = await params;
  const memberId = parseInt(idString);

  // try {
  // const profileResponse = await client.GET("/api/members/{memberId}", {
  //     params: {
  //         path: { memberId },
  //     },
  //     headers: {
  //         cookie: (await cookies()).toString(),
  //     },
  // });

  const followingResponse = await client.GET(
    "/api/follows/{memberId}/followings",
    {
      params: {
        path: { memberId },
      },
      headers: {
        cookie: (await cookies()).toString(),
      },
    }
  );

  const followerResponse = await client.GET(
    "/api/follows/{memberId}/followers",
    {
      params: {
        path: { memberId },
      },
      headers: {
        cookie: (await cookies()).toString(),
      },
    }
  );

  const allMembersResponse = await client.GET("/api/members/totalMember", {
    headers: {
      cookie: (await cookies()).toString(),
    },
  });
  const followingList = followingResponse?.data?.data ?? [];
  const followerList = followerResponse?.data?.data ?? [];
  const allMembers = allMembersResponse?.data?.data ?? [];
  // const followingList = followingResponse.data.data;
  // const followerList = followerResponse.data.data;
  // const allMembers = allMembersResponse.data.data;
  const totalPages = 3;
  // const profileData = profileResponse.data.data;

  return (
    <ClientPage
      memberId={memberId}
      followingList={followingList}
      followerList={followerList}
      allMembers={allMembers}
      totalPages={totalPages}
      // profileData={profileData}
    />
  );
  // } catch (error) {
  //     console.error("데이터 로딩 중 오류 발생:", error);
  //     return <div>데이터 로딩 중 오류 발생</div>;
  // }
}
