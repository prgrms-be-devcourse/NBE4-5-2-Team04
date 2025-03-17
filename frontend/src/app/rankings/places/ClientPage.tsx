"use client";

import { useState, useEffect } from "react";
import Link from "next/link";
import { client } from "@/lib/backend/client";

export default function ClientPlacesList({
  places: initialPlaces,
  totalPages: initialTotalPages,
}) {
  const [places, setPlaces] = useState(initialPlaces);
  const [totalPages, setTotalPages] = useState(initialTotalPages);
  const [period, setPeriod] = useState("ONE_MONTH");
  const [searchTerm, setSearchTerm] = useState("");
  const [loading, setLoading] = useState(false);
  const [page, setPage] = useState(0);

  const fetchPlaces = async (selectedPeriod, placeName = "", pageNum = 0) => {
    setLoading(true);
    try {
      const res = await client.GET("/api/rankings/places", {
        params: {
          query: { period: selectedPeriod, placeName, page: pageNum, size: 5 },
        },
        credentials: "include",
      });

      if (res?.data?.data) {
        setPlaces(res.data.data.content);
        setTotalPages(res.data.data.totalPages);
      } else {
        setPlaces([]);
        setTotalPages(1);
      }
    } catch (error) {
      console.error("인기 장소 데이터 로딩 실패:", error);
      setPlaces([]);
      setTotalPages(1);
    }
    setLoading(false);
  };

  const handlePeriodChange = (e) => {
    const newPeriod = e.target.value;
    setPeriod(newPeriod);
    setPage(0);
    fetchPlaces(newPeriod, searchTerm, 0);
  };

  useEffect(() => {
    const delayDebounceFn = setTimeout(() => {
      fetchPlaces(period, searchTerm, 0);
    }, 300);

    return () => clearTimeout(delayDebounceFn);
  }, [searchTerm]);

  const handlePageChange = (newPage) => {
    if (newPage >= 0 && newPage < totalPages) {
      setPage(newPage);
      fetchPlaces(period, searchTerm, newPage);
    }
  };

  return (
    <div className="p-6 max-w-4xl mx-auto">
      <div className="mb-4 flex justify-between items-center">
        <h1 className="text-2xl font-bold">인기 장소 목록</h1>

        <div className="flex gap-2">
          <input
            type="text"
            placeholder="장소 검색..."
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
            className="w-48 p-2 border rounded-lg shadow-md text-sm"
          />

          <select
            value={period}
            onChange={handlePeriodChange}
            className="w-32 p-2 border rounded-lg bg-white shadow-md text-sm"
          >
            <option value="ONE_MONTH">1개월</option>
            <option value="THREE_MONTHS">3개월</option>
            <option value="SIX_MONTHS">6개월</option>
          </select>
        </div>
      </div>

      {loading ? (
        <p className="text-center text-gray-500">데이터 불러오는 중...</p>
      ) : places.length === 0 ? (
        <p className="text-center text-gray-500">검색 결과가 없습니다.</p>
      ) : (
        <>
          <ul className="space-y-4">
            {places.map((place) => (
              <li
                key={place.placeId}
                className="p-7 bg-gray-100 rounded-lg shadow hover:bg-gray-200 transition"
              >
                <Link href={`/rankings/places/${place.placeId}/posts`}>
                  <p className="text-xl font-semibold">{place.placeName}</p>
                  <p className="text-sm text-gray-600">지역: {place.region}</p>
                  <p className="text-sm">
                    좋아요 {place.likeCount} | 스크랩 {place.scrapCount} |
                    게시글 {place.postCount}
                  </p>
                </Link>
              </li>
            ))}
          </ul>

          <div className="mt-6 flex justify-center gap-4">
            <button
              onClick={() => handlePageChange(page - 1)}
              disabled={page === 0}
              className={`px-4 py-2 border rounded-lg ${
                page === 0
                  ? "text-gray-400 cursor-not-allowed"
                  : "hover:bg-gray-200"
              }`}
            >
              이전
            </button>
            <span className="text-sm text-gray-600">
              {page + 1} / {totalPages}
            </span>
            <button
              onClick={() => handlePageChange(page + 1)}
              disabled={page === totalPages - 1}
              className={`px-4 py-2 border rounded-lg ${
                page === totalPages - 1
                  ? "text-gray-400 cursor-not-allowed"
                  : "hover:bg-gray-200"
              }`}
            >
              다음
            </button>
          </div>
        </>
      )}
    </div>
  );
}
