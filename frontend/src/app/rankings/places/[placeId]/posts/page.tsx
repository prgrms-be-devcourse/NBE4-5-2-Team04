import { client } from "@/lib/backend/client";
import { cookies } from "next/headers";
import ClientPostList from "./ClientPostList";

export default async function PlacePostsPage({
  params,
}: {
  params: { placeId: string };
}) {
  const { placeId } = params;
  const numPlaceId = Number(placeId);
  const cookieString = (await cookies()).toString();

  let posts = [];
  let totalPages = 1;

  try {
    const res = await client.GET(`/api/rankings/places/${numPlaceId}/posts`, {
      params: {
        query: { period: "ONE_MONTH", page: 0, size: 5 },
      },
      headers: { cookie: cookieString },
    });

    if (res?.data?.data) {
      posts = res.data.data.content;
      totalPages = res.data.data.totalPages;
    } else {
      console.warn("게시글 목록이 없음:", res?.data?.data);
      posts = [];
      totalPages = 1;
    }
  } catch (error) {
    console.error("게시글 데이터 로딩 실패:", error);
  }

  return (
    <ClientPostList
      placeId={numPlaceId}
      initialPosts={posts}
      totalPages={totalPages}
    />
  );
}
