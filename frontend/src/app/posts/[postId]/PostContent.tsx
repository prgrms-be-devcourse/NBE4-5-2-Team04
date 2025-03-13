"use client";

import { useState } from "react";
import { Button } from "@/components/ui/button";
import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar";
import { components } from "@/lib/backend/schema";
import { client } from "@/lib/backend/client";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faBookmark, faHeart } from "@fortawesome/free-solid-svg-icons";

export default function PostContent({
  post,
}: {
  post: components["schemas"]["PostDetailResponseDTO"];
}) {
  const [isLiked, setIsLiked] = useState(post.isLiked);
  const [likeCount, setLikeCount] = useState(post.likeCount);
  const [isScrapped, setIsScrapped] = useState(post.isScrapped);
  const [scrapCount, setScrapCount] = useState(post.scrapCount);

  const handleLike = async () => {
    setIsLiked(!isLiked);
    setLikeCount(isLiked ? likeCount - 1 : likeCount + 1);
    await client.POST(`/api/posts/${post.id}/likes`, {
      credentials: "include",
    });
  };

  const handleScrap = async () => {
    setIsScrapped(!isScrapped);
    setScrapCount(isScrapped ? scrapCount - 1 : scrapCount + 1);
    await client.POST(`/api/posts/${post.id}/scrap`, {
      credentials: "include",
    });
  };

  return (
    <div>
      <h1 className="text-2xl font-bold">{post.title}</h1>
      <p className="text-sm text-gray-500">
        {new Date(post.createdDate!).toLocaleDateString()}
      </p>

      <div className="flex items-center mt-4 space-x-3">
        <Avatar>
          <AvatarImage
            src={post.authorDTO!.profileImageUrl ?? ""}
            alt={post.authorDTO!.nickname}
          />
          <AvatarFallback>{post.authorDTO!.nickname!.charAt(0)}</AvatarFallback>
        </Avatar>
        <div>
          <p className="font-semibold">{post.authorDTO!.nickname}</p>
        </div>
        <Button variant="outline">팔로우</Button>
      </div>

      <p className="mt-4">{post.content}</p>

      <div className="flex mt-4 space-x-6 text-gray-600">
        <button
          onClick={handleLike}
          className={isLiked ? "text-yellow-500" : "text-gray-500"}
        >
          <FontAwesomeIcon
            icon={faHeart}
            className={`w-5 h-5 ${isLiked ? "text-red-500" : "text-gray-400"}`}
          />
          좋아요 {likeCount}
        </button>
        <button
          onClick={handleScrap}
          className={isScrapped ? "text-yellow-500" : "text-gray-500"}
        >
          <FontAwesomeIcon
            icon={faBookmark}
            className={`w-5 h-5 ${
              isScrapped ? "text-blue-500" : "text-gray-400"
            }`}
          />
          스크랩 {scrapCount}
        </button>
      </div>
    </div>
  );
}
