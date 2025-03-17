import { client } from "@/lib/backend/client";
import { cookies } from "next/headers";
import ClientPage from "./ClientPage";

export default async function PopularPlacesPage() {
  let places = [];
  let totalPages = 1;
  const cookieString = (await cookies()).toString();

  try {
    const res = await client.GET("/api/rankings/places", {
      params: { query: { period: "ONE_MONTH", page: 0, size: 5 } },
      headers: { cookie: cookieString },
    });

    if (res?.data?.data) {
      places = res.data.data.content;
      totalPages = res.data.data.totalPages || 1;
    }
  } catch (error) {
    console.error("❌ 인기 장소 데이터 로딩 실패:", error);
  }

  return <ClientPage places={places} totalPages={totalPages} />;
}
