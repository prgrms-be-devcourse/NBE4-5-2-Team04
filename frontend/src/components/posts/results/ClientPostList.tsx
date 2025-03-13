"use client";

import { useEffect, useRef, useState } from "react";
import { useRouter } from "next/navigation";
import { useInfiniteQuery } from "@tanstack/react-query";
import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar";
import {
  Carousel,
  CarouselContent,
  CarouselItem,
  CarouselNext,
  CarouselPrevious,
} from "@/components/ui/carousel";
import { Card, CardContent } from "@/components/ui/card";
import Image from "next/image";
import { client } from "@/lib/backend/client";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faBookmark, faHeart } from "@fortawesome/free-solid-svg-icons";

type Post = {
  id: number;
  title: string;
  content: string;
  placeDTO: { placeName: string; category: string };
  likeCount: number;
  scrapCount: number;
  commentCount: number;
  imageUrls: string[];
  author: {
    memberId: number;
    nickname: string;
    profileImageUrl: string | null;
  };
  isLiked: boolean;
  isScrapped: boolean;
};

interface PostListProps {
  queryKey?: string;
  apiEndpoint: string;
  placeName?: string;
  category?: string;
  memberId?: string;
}

export default function PostList({
  queryKey,
  apiEndpoint,
  placeName,
  category,
  memberId,
}: PostListProps) {
  const router = useRouter();
  const [activeIndices, setActiveIndices] = useState<Record<number, number>>(
    {}
  );
  const [likes, setLikes] = useState<
    Record<number, { isLiked: boolean; count: number }>
  >({});
  const [scraps, setScraps] = useState<
    Record<number, { isScrapped: boolean; count: number }>
  >({});

  const fetchPosts = async ({ pageParam = 0 }) => {
    const { data, error } = await client.GET(apiEndpoint, {
      params: {
        path: { memberId } || null,
        query: {
          page: pageParam,
          size: 5,
          placeName: placeName || null,
          category: category || null,
        },
      },
      credentials: "include",
    });

    if (error) {
      throw new Error("게시글을 불러오는 데 실패했습니다.");
    }
    return data.data;
  };

  const { data, fetchNextPage, hasNextPage, isFetchingNextPage } =
    useInfiniteQuery({
      queryFn: fetchPosts,
      getNextPageParam: (lastPage) => {
        return lastPage.last === false ? lastPage.number! + 1 : undefined;
      },
      initialPageParam: 0,
      queryKey: [queryKey, placeName, category],
    });

  const observerRef = useRef(null);

  useEffect(() => {
    if (!hasNextPage) return;
    const observer = new IntersectionObserver(
      (entries) => {
        if (entries[0].isIntersecting) {
          fetchNextPage();
        }
      },
      { threshold: 1.0 }
    );

    if (observerRef.current) observer.observe(observerRef.current);
    return () => observer.disconnect();
  }, [hasNextPage, fetchNextPage]);

  useEffect(() => {
    if (data?.pages) {
      const newLikes: Record<number, { isLiked: boolean; count: number }> = {};
      const newScraps: Record<number, { isScrapped: boolean; count: number }> =
        {};

      data.pages.forEach((page) => {
        page.content?.forEach((post: Post) => {
          newLikes[post.id] = {
            isLiked: post.isLiked,
            count: post.likeCount,
          };
          newScraps[post.id] = {
            isScrapped: post.isScrapped,
            count: post.scrapCount,
          };
        });
      });

      setLikes(newLikes);
      setScraps(newScraps);
    }
  }, [data]);

  const handleLike = async (postId: number) => {
    setLikes((prev) => {
      const previousLike = prev[postId] || { isLiked: false, count: 0 };

      return {
        ...prev,
        [postId]: {
          isLiked: !previousLike.isLiked,
          count: previousLike.isLiked
            ? previousLike.count - 1
            : previousLike.count + 1,
        },
      };
    });

    await client.POST(`/api/posts/${postId}/likes`, { credentials: "include" });
  };

  const handleScrap = async (postId: number) => {
    setScraps((prev) => {
      const previousScrap = prev[postId] || { isScrapped: false, count: 0 };

      return {
        ...prev,
        [postId]: {
          isScrapped: !previousScrap.isScrapped,
          count: previousScrap.isScrapped
            ? previousScrap.count - 1
            : previousScrap.count + 1,
        },
      };
    });

    await client.POST(`/api/posts/${postId}/scrap`, { credentials: "include" });
  };

  return (
    <div className="max-w-2xl mx-auto space-y-4">
      {data?.pages.map((page) =>
        page.content?.map((post: Post) => (
          <Card key={post.id} className="p-4 w-full">
            <CardContent>
              {/* 사용자 정보 */}
              <div className="flex items-center space-x-3">
                <Avatar>
                  {post.author.profileImageUrl ? (
                    <AvatarImage
                      src={
                        post.author.profileImageUrl &&
                        post.author.profileImageUrl !== "/default-profile.png"
                          ? process.env.NEXT_PUBLIC_BASE_URL +
                            post.author.profileImageUrl
                          : "/default-profile.png"
                      }
                      alt={post.author.nickname}
                    />
                  ) : (
                    <AvatarFallback>{post.author.nickname[0]}</AvatarFallback>
                  )}
                </Avatar>
                <div>
                  <p className="font-semibold">{post.author.nickname}</p>
                  <p className="text-xs text-gray-500">
                    {post.placeDTO.placeName} • {post.placeDTO.category}
                  </p>
                </div>
              </div>

              {/* 게시글 내용 (클릭 시 페이지 이동) */}
              <p
                className="mt-2 cursor-pointer text-blue-600 hover:underline"
                onClick={() => router.push(`/posts/${post.id}`)}
              >
                {post.content}
              </p>

              {/* 이미지 Carousel */}
              {post.imageUrls.length > 0 && (
                <Carousel className="mt-2 w-full h-auto p-3">
                  <CarouselContent>
                    {post.imageUrls.map((img, index) => (
                      <CarouselItem
                        key={index}
                        className="flex justify-center items-center max-w-[500px] mx-auto"
                        onClick={(e) => {
                          e.stopPropagation();
                          setActiveIndices((prev) => ({
                            ...prev,
                            [post.id]: index,
                          }));
                        }}
                      >
                        <Image
                          src={process.env.NEXT_PUBLIC_BASE_URL + img}
                          alt="post image"
                          className="rounded-lg object-cover"
                          width={200}
                          height={200}
                          layout="intrinsic"
                          unoptimized
                        />
                      </CarouselItem>
                    ))}
                  </CarouselContent>
                  <CarouselPrevious />
                  <CarouselNext />

                  {/* 인디케이터 */}
                  <div className="flex justify-center space-x-2 mt-2">
                    {post.imageUrls.map((_, index) => (
                      <span
                        key={index}
                        className={`h-2 w-2 rounded-full ${
                          index === (activeIndices[post.id] || 0)
                            ? "bg-blue-500"
                            : "bg-gray-400"
                        }`}
                      ></span>
                    ))}
                  </div>
                </Carousel>
              )}

              {/* 좋아요, 댓글, 스크랩 */}
              <div className="mt-2 flex justify-start space-x-4 text-sm">
                <button
                  className={`flex items-center cursor-pointer ${
                    likes[post.id]?.isLiked
                      ? "text-yellow-500"
                      : "text-gray-500"
                  }`}
                  onClick={(e) => {
                    e.stopPropagation();
                    handleLike(post.id);
                  }}
                >
                  <FontAwesomeIcon
                    icon={faHeart}
                    className={`w-5 h-5 ${
                      likes[post.id]?.isLiked ? "text-red-500" : "text-gray-400"
                    }`}
                  />{" "}
                  좋아요 {likes[post.id]?.count ?? post.likeCount}
                </button>
                <button
                  className={`flex items-center cursor-pointer ${
                    scraps[post.id]?.isScrapped
                      ? "text-yellow-500"
                      : "text-gray-500"
                  }`}
                  onClick={(e) => {
                    e.stopPropagation();
                    handleScrap(post.id);
                  }}
                >
                  <FontAwesomeIcon
                    icon={faBookmark}
                    className={`w-5 h-5 ${
                      scraps[post.id]?.isScrapped
                        ? "text-blue-500"
                        : "text-gray-400"
                    }`}
                  />{" "}
                  스크랩 {scraps[post.id]?.count ?? post.scrapCount}
                </button>
                <span className="text-gray-500">댓글 {post.commentCount}</span>
              </div>
            </CardContent>
          </Card>
        ))
      )}

      {/* 무한 스크롤 트리거 */}
      <div
        ref={observerRef}
        className="h-10 w-full flex justify-center items-center"
      >
        {isFetchingNextPage && <p>로딩 중...</p>}
      </div>
    </div>
  );
}
