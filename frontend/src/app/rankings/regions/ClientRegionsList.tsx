"use client";

import { useState } from "react";
import Link from "next/link";
import { client } from "@/lib/backend/client";

export default function ClientRegionsList({ regions: initialRegions }) {
  const [regions, setRegions] = useState(initialRegions);
  const [period, setPeriod] = useState("ONE_MONTH");
  const [loading, setLoading] = useState(false);

  const fetchRegionsByPeriod = async (selectedPeriod) => {
    setPeriod(selectedPeriod);
    setLoading(true);

    try {
      const res = await client.GET("/api/rankings/regions", {
        params: { query: { period: selectedPeriod } },
        credentials: "include",
      });

      if (Array.isArray(res?.data?.data?.content)) {
        setRegions(res.data.data.content);
      } else {
        setRegions([]);
      }
    } catch (error) {
      console.error("인기 지역 데이터 로딩 실패:", error);
      setRegions([]);
    }

    setLoading(false);
  };

  return (
    <div className="p-6 max-w-4xl mx-auto">
      <div className="mb-4 flex justify-between items-center">
        <h1 className="text-2xl font-bold">인기 지역 순위</h1>
        <select
          value={period}
          onChange={(e) => fetchRegionsByPeriod(e.target.value)}
          className="w-32 p-1 border rounded-lg bg-white shadow-md text-sm"
        >
          <option value="ONE_MONTH">1개월</option>
          <option value="THREE_MONTHS">3개월</option>
          <option value="SIX_MONTHS">6개월</option>
        </select>
      </div>

      {loading ? (
        <p className="text-center text-gray-500">데이터 불러오는 중...</p>
      ) : regions.length === 0 ? (
        <p className="text-center text-gray-500">인기 지역이 없습니다.</p>
      ) : (
        <ul className="space-y-4">
          {regions.map((region, index) => {
            const rankColors = [
              "bg-gradient-to-r from-yellow-400 to-red-500 text-white shadow-lg",
              "bg-gradient-to-r from-yellow-400 to-red-500 text-white shadow-lg",
              "bg-gradient-to-r from-yellow-400 to-red-500 text-white shadow-lg",
            ];
            const rankStyle =
              index <= 2
                ? rankColors[index]
                : "bg-gray-200 text-gray-700 border border-gray-400";

            return (
              <li
                key={region.region}
                className="p-6 bg-white rounded-lg shadow-lg hover:shadow-xl transition flex items-center border border-gray-300"
              >
                <div
                  className={`w-12 h-12 flex items-center justify-center font-bold text-lg rounded-full mr-5 ${rankStyle}`}
                >
                  {index + 1}
                </div>

                <Link
                  href={{
                    pathname: `/rankings/regions/${region.region}/posts`,
                    query: { regionName: region.regionName },
                  }}
                  className="flex-1"
                >
                  <p className="text-xl font-semibold">{region.regionName}</p>
                  <p className="text-sm text-gray-600">
                    좋아요 {region.likeCount} | 스크랩 {region.scrapCount} |
                    게시글 {region.postCount}
                  </p>
                </Link>
              </li>
            );
          })}
        </ul>
      )}
    </div>
  );
}
