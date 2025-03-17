"use client";

import { useState } from "react";
import Link from "next/link";
import { client } from "@/lib/backend/client";

export default function ClientRegionPlaces({
  region,
  regionName,
  initialPosts,
  totalPages: initialTotalPages,
}) {
  const [posts, setPosts] = useState(initialPosts);
  const [filteredPosts, setFilteredPosts] = useState(initialPosts); // 🔥 검색 필터링용
  const [totalPages, setTotalPages] = useState(initialTotalPages);
  const [period, setPeriod] = useState("ONE_MONTH");
  const [page, setPage] = useState(0);
  const [loading, setLoading] = useState(false);
  const [searchTerm, setSearchTerm] = useState("");

  const fetchPosts = async (newPage, selectedPeriod) => {
    if (newPage < 0 || newPage >= totalPages) return;

    setLoading(true);
    try {
      const res = await client.GET(`/api/rankings/regions/${region}/posts`, {
        params: { query: { period: selectedPeriod, page: newPage, size: 5 } },
        credentials: "include",
      });

      if (res?.data?.data) {
        setPosts(res.data.data.content);
        setFilteredPosts(res.data.data.content);
        setTotalPages(res.data.data.totalPages);
        setPage(newPage);
      }
    } catch (error) {
      console.error(`${region} 지역 게시글 불러오기 실패:`, error);
    }
    setLoading(false);
  };

  const handleSearch = (e) => {
    const value = e.target.value.toLowerCase();
    setSearchTerm(value);

    if (!value) {
      setFilteredPosts(posts);
    } else {
      const filtered = posts.filter((post) =>
        post.title.toLowerCase().includes(value)
      );
      setFilteredPosts(filtered);
    }
  };

  return (
    <div className="p-6 max-w-4xl mx-auto">
      <div className="mb-4 flex justify-between items-center">
        <h1 className="text-2xl font-bold">{regionName} 지역 게시글 목록</h1>

        <div className="flex gap-2">
          <input
            type="text"
            placeholder="게시글 검색..."
            value={searchTerm}
            onChange={handleSearch}
            className="w-48 p-2 border rounded-lg shadow-md text-sm"
          />

          <select
            value={period}
            onChange={(e) => {
              setPeriod(e.target.value);
              fetchPosts(0, e.target.value);
            }}
            className="w-32 p-2 border rounded-lg shadow-md text-sm"
          >
            <option value="ONE_MONTH">1개월</option>
            <option value="THREE_MONTHS">3개월</option>
            <option value="SIX_MONTHS">6개월</option>
          </select>
        </div>
      </div>

      {loading ? (
        <p className="text-center text-gray-500">데이터 불러오는 중...</p>
      ) : filteredPosts.length === 0 ? (
        <p className="text-center text-gray-500">
          {region} 지역의 게시글이 없습니다.
        </p>
      ) : (
        <>
          <ul className="space-y-4">
            {filteredPosts.map((post) => (
              <li
                key={post.id}
                className="p-6 bg-gray-100 rounded-lg shadow hover:bg-gray-200 transition"
              >
                <Link href={`/posts/${post.id}`}>
                  <h3 className="text-lg font-semibold">{post.title}</h3>
                  <p className="text-sm text-gray-600 line-clamp-2">
                    {post.content}
                  </p>
                  <div className="mt-2 flex justify-start gap-x-4 text-sm text-gray-500">
                    <span>좋아요 {post.likeCount}</span>
                    <span>스크랩 {post.scrapCount}</span>
                    <span>댓글 {post.commentCount}</span>
                  </div>
                </Link>
              </li>
            ))}
          </ul>

          <div className="mt-6 flex justify-center gap-4">
            <button
              onClick={() => fetchPosts(page - 1, period)}
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
              onClick={() => fetchPosts(page + 1, period)}
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
