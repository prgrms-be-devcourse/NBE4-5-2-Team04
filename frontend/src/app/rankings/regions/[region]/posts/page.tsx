import { client } from "@/lib/backend/client";
import { cookies } from "next/headers";
import ClientRegionPlaces from "./ClientRegionPlaces";

export default async function RegionPostsPage({
  params,
}: {
  params: { region: string };
}) {
  if (!params?.region) {
    return <div>잘못된 요청입니다.</div>;
  }

  const region = decodeURIComponent(params.region);
  const cookieString = (await cookies()).toString();

  let posts = [];
  let totalPages = 1;
  let regionName = region;

  try {
    const regionRes = await client.GET("/api/rankings/regions", {
      params: { query: { period: "ONE_MONTH" } },
      headers: { cookie: cookieString },
    });

    if (regionRes?.data?.data?.content) {
      const matchedRegion = regionRes.data.data.content.find(
        (r) => r.region === region
      );
      if (matchedRegion) {
        regionName = matchedRegion.regionName;
      }
    }

    const postRes = await client.GET(`/api/rankings/regions/${region}/posts`, {
      params: { query: { period: "ONE_MONTH", page: 0, size: 5 } },
      headers: { cookie: cookieString },
    });

    if (postRes?.data?.data) {
      posts = postRes.data.data.content;
      totalPages = postRes.data.data.totalPages;
    }
  } catch (error) {
    console.error(`${region} 지역 게시글 데이터 로딩 실패:`, error);
  }

  return (
    <ClientRegionPlaces
      region={region}
      regionName={regionName}
      initialPosts={posts}
      totalPages={totalPages}
    />
  );
}
