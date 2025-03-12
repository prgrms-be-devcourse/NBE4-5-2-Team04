"use client";

import {useState} from "react";
import PostList from "@/components/posts/results/ClientPostList";

export default function SearchablePostList({queryKey, apiEndpoint}: { queryKey: string; apiEndpoint: string }) {
    const [placeName, setPlaceName] = useState<string | null>(null);
    const [category, setCategory] = useState<string | null>(null);
    const [searchParams, setSearchParams] = useState<{ placeName: string | null; category: string | null }>({
        placeName: null,
        category: null,
    });

    const handleSearch = () => {
        setSearchParams({
            placeName: placeName || null,
            category: category || null,
        });
    };

    return (
        <div className="max-w-2xl mx-auto space-y-4">
            <div className="flex space-x-2 mb-4">
                <input
                    type="text"
                    placeholder="장소명"
                    value={placeName || ""}
                    onChange={(e) => setPlaceName(e.target.value || null)}
                    className="border p-2 rounded-md w-full"
                />
                <input
                    type="text"
                    placeholder="카테고리"
                    value={category || ""}
                    onChange={(e) => setCategory(e.target.value || null)}
                    className="border p-2 rounded-md w-full"
                />
                <button onClick={handleSearch} className="bg-blue-500 text-white px-4 py-2 rounded-md">
                    검색
                </button>
            </div>

            <PostList queryKey={queryKey} apiEndpoint={apiEndpoint} placeName={searchParams.placeName}
                      category={searchParams.category}/>
        </div>
    );
}