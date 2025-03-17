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
          path: {memberId},
          query: {
              pageable: {
                  page: undefined,
                  size: undefined,
                  sort: undefined
              }
          }
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
  const followerList = followerResponse?.data?.data.content ?? [];
  const allMembers = allMembersResponse?.data?.data ?? [];
  // const followingList = followingResponse.data.data;
  // const followerList = followerResponse.data.data;
  // const allMembers = allMembersResponse.data.data;
  const totalPages = followerResponse?.data?.data?.totalPages;
  const totalElements=followerResponse?.data?.data?.totalElements;
  const onepageElements=followerList?.length;
  // const profileData = profileResponse.data.data;
    console.log("전체 페이지 수:", followerResponse?.data?.data?.totalPages);
    console.log("현재 페이지 번호:", followerResponse?.data?.data?.pageable?.pageNumber);
    console.log("팔로워 수 (followerList.length):", followerList?.length);
    console.log("전체 요소 수 (totalElements):", followerResponse?.data?.data?.totalElements);

  return (
    <ClientPage
      memberId={memberId}
      followingList={followingList}
      followerList={followerList}
      allMembers={allMembers}
      totalPages={totalPages || 1}
      totalElements={totalElements}
        onepageElements={onepageElements}
      // profileData={profileData}
    />
  );
  // } catch (error) {
  //     console.error("데이터 로딩 중 오류 발생:", error);
  //     return <div>데이터 로딩 중 오류 발생</div>;
  // }
}
