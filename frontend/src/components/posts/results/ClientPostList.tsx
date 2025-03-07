"use client";

import { useEffect, useRef } from "react";
import { useInfiniteQuery } from "@tanstack/react-query";
import { Avatar, AvatarImage, AvatarFallback } from "@/components/ui/avatar";
import { Carousel, CarouselContent, CarouselItem, CarouselNext, CarouselPrevious } from "@/components/ui/carousel";
import { Card, CardContent } from "@/components/ui/card";
import Image from "next/image";

type Post = {
    id: number;
    title: string;
    content: string;
    placeDTO: { placeName: string; category: string };
    likeCount: number;
    scrapCount: number;
    commentCount: number;
    imageUrls: string[];
    author: { memberId: number; nickname: string; profileImageUrl: string | null };
};

interface PostListProps {
    queryKey?: string;
    fetchFunction?: ({ pageParam }: { pageParam?: number }) => Promise<{
        totalElements?: number;
        totalPages?: number;
        first?: boolean;
        last?: boolean;
        numberOfElements?: number;
        size?: number;
        number?: number;
        content?: Post[];
        empty?: boolean;
    }>;
}

export default function PostList({ queryKey, fetchFunction }: PostListProps) {
    const { data, fetchNextPage, hasNextPage,isFetchingNextPage } = useInfiniteQuery({
        queryFn: fetchFunction,
        getNextPageParam: (lastPage) => {
            return lastPage.last === false ? lastPage.number! + 1 : undefined;
        },
        initialPageParam: 0,
        queryKey: [queryKey],
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

    return (
        <div className="max-w-2xl mx-auto space-y-4">
            {data?.pages.map((page) =>
                page.content?.map((post: Post) => (
                    <Card key={post.id} className="p-4">
                        <CardContent>
                            {/* 사용자 정보 */}
                            <div className="flex items-center space-x-3">
                                <Avatar>
                                    {post.author.profileImageUrl ? (
                                        <AvatarImage src={post.author.profileImageUrl} alt={post.author.nickname} />
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

                            {/* 게시글 내용 */}
                            <p className="mt-2">{post.content}</p>

                            {/* 이미지 Carousel */}
                            {post.imageUrls.length > 0 && (
                                <Carousel className="mt-2 w-full h-48">
                                    <CarouselContent>
                                        {post.imageUrls.map((img, index) => (
                                            <CarouselItem key={index} className="flex justify-center items-center">
                                                <Image src={img} alt="post image" width={500} height={300} className="rounded-lg object-cover" />
                                            </CarouselItem>
                                        ))}
                                    </CarouselContent>
                                    <CarouselPrevious />
                                    <CarouselNext />
                                </Carousel>
                            )}

                            {/* 좋아요, 댓글, 스크랩 */}
                            <div className="mt-2 flex justify-start space-x-4 text-sm text-gray-500">
                                <span>좋아요 {post.likeCount}</span>
                                <span>댓글 {post.commentCount}</span>
                                <span>스크랩 {post.scrapCount}</span>
                            </div>
                        </CardContent>
                    </Card>
                ))
            )}

            {/* 무한 스크롤 트리거 */}
            <div ref={observerRef} className="h-10 w-full flex justify-center items-center">
                {isFetchingNextPage && <p>로딩 중...</p>}
            </div>
        </div>
    );
}