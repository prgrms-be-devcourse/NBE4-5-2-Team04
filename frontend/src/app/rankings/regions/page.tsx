import { client } from "@/lib/backend/client";
import { cookies } from "next/headers";
import ClientRegionsList from "./ClientRegionsList";

export default async function PopularRegionsPage() {
  let regions = [];
  const cookieString = (await cookies()).toString();

  try {
    const res = await client.GET("/api/rankings/regions", {
      params: { query: { period: "ONE_MONTH" } },
      headers: { cookie: cookieString },
    });

    if (Array.isArray(res?.data?.data?.content)) {
      regions = res.data.data.content;
    }
  } catch (error) {
    console.error("인기 지역 데이터 로딩 실패:", error);
  }

  return <ClientRegionsList regions={regions ?? []} />;
}
