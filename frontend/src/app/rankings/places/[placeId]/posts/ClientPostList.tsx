"use client";

import { useState } from "react";
import Link from "next/link";
import { client } from "@/lib/backend/client";

export default function ClientPostList({
  placeId,
  initialPosts,
  totalPages: initialTotalPages,
}) {
  const [posts, setPosts] = useState(initialPosts);
  const [filteredPosts, setFilteredPosts] = useState(initialPosts);
  const [totalPages, setTotalPages] = useState(initialTotalPages);
  const [page, setPage] = useState(0);
  const [loading, setLoading] = useState(false);
  const [searchTerm, setSearchTerm] = useState("");

  const fetchPostsByPage = async (newPage) => {
    if (newPage < 0 || newPage >= totalPages) return;

    setLoading(true);
    try {
      const res = await client.GET(`/api/rankings/places/${placeId}/posts`, {
        params: {
          query: { period: "ONE_MONTH", page: newPage, size: 5 },
        },
        credentials: "include",
      });

      if (res?.data?.data) {
        setPosts(res.data.data.content);
        setFilteredPosts(res.data.data.content);
        setTotalPages(res.data.data.totalPages);
        setPage(newPage);
      }
    } catch (error) {
      console.error("게시글 데이터 로딩 실패:", error);
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

  if (!filteredPosts || filteredPosts.length === 0) {
    return (
      <div className="p-6">
        <h1 className="text-2xl font-bold mb-4">게시글 목록</h1>
        <input
          type="text"
          placeholder="게시글 검색..."
          value={searchTerm}
          onChange={handleSearch}
          className="w-full p-2 border rounded-lg shadow-md text-sm mb-4"
        />
        <p>이 장소에 등록된 게시글이 없습니다.</p>
      </div>
    );
  }

  return (
    <div className="p-6">
      <h1 className="text-2xl font-bold mb-4">게시글 목록</h1>
      <input
        type="text"
        placeholder="게시글 검색..."
        value={searchTerm}
        onChange={handleSearch}
        className="w-full p-2 border rounded-lg shadow-md text-sm mb-4"
      />

      <ul className="space-y-4">
        {filteredPosts.map((post) => (
          <li
            key={post.id}
            className="border rounded-lg p-4 shadow-sm hover:shadow-md transition"
          >
            <Link href={`/posts/${post.id}`} className="block">
              <div className="flex items-center gap-4 mb-2">
                <img
                  src={post.author.profileImageUrl || "/default-profile.png"}
                  alt="작성자 프로필"
                  className="w-10 h-10 rounded-full border"
                />

                <div>
                  <p className="text-sm font-semibold">
                    {post.author.nickname}
                  </p>
                </div>
              </div>

              <h3 className="text-lg font-semibold mt-2">{post.title}</h3>
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
          onClick={() => fetchPostsByPage(page - 1)}
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
          onClick={() => fetchPostsByPage(page + 1)}
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
    </div>
  );
}
