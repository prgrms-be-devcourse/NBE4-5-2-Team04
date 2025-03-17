"use client";

import {useState} from "react";
import {Button} from "@/components/ui/button";
import {Avatar, AvatarFallback, AvatarImage} from "@/components/ui/avatar";
import {components} from "@/lib/backend/schema";
import {client} from "@/lib/backend/client";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {faBookmark, faHeart} from "@fortawesome/free-solid-svg-icons";
import {Carousel, CarouselContent, CarouselItem, CarouselNext, CarouselPrevious} from "@/components/ui/carousel";
import Image from "next/image";
import Link from "next/link";

export default function PostContent({
                                        post,
                                    }: {
    post: components["schemas"]["PostDetailResponseDTO"];
}) {
    const [isLiked, setIsLiked] = useState(post.isLiked);
    const [likeCount, setLikeCount] = useState(post.likeCount);
    const [isScrapped, setIsScrapped] = useState(post.isScrapped);
    const [scrapCount, setScrapCount] = useState(post.scrapCount);
    const [activeIndices, setActiveIndices] = useState<number>(0);


    const handleLike = async () => {
        if (!post.id) {
            console.error("post.id가 없습니다.");
            alert("게시글 정보를 확인할 수 없습니다.");
            return;
        }

        try {
            const res = await client.POST("/api/posts/{postId}/likes", {
                params: {
                    path: {postId: post.id},
                },
                credentials: "include",
            });

            console.log(res);
            if (res?.data?.data && "liked" in res.data.data) {
                setIsLiked(res.data.data.liked);
                setLikeCount(res.data.data.likeCount);
            }
        } catch (error) {
            console.error("좋아요 요청 실패:", error);
            alert("좋아요 처리 중 오류가 발생했습니다.");
        }
    };

    const handleScrap = async () => {
        if (!post.id) {
            console.error("post.id가 없습니다.");
            alert("게시글 정보를 확인할 수 없습니다.");
            return;
        }

        try {
            const res = await client.POST("/api/posts/{postId}/scraps", {
                params: {
                    path: {postId: post.id},
                },
                credentials: "include",
            });

            console.log(res);
            if (res?.data?.data && "scrapped" in res.data.data) {
                setIsScrapped(res.data.data.scrapped);
                setScrapCount(res.data.data.scrapCount);
            }
        } catch (error) {
            console.error("스크랩 요청 실패:", error);
            alert("스크랩 처리 중 오류가 발생했습니다.");
        }
    };

    const handleFollowToggle = async (userId: number) => {
        if (!post.id) {
            console.error("post.id가 없습니다.");
            alert("게시글 정보를 확인할 수 없습니다.");
            return;
        }

        try {


            const requestBody: components["schemas"]["FollowRequestDto"] = {

                followingId: userId,
            };

            await client.POST("/api/posts/{postId}/follow", { // 엔드포인트 수정 및 postId 사용
                body: requestBody,
                params: { // params 속성 추가
                    path: { postId: post.id }, // postId 설정
                },
                credentials: "include",
            });

            console.log("팔로우 토글 성공");

        } catch (error) {
            console.error("팔로우/언팔로우 실패:", error);
            alert("팔로우 처리 중 오류가 발생했습니다.");
        }
    };

    return (
        <div>
            <h1 className="text-2xl font-bold">{post.title}</h1>
            <p className="text-sm text-gray-500">
                {new Date(post.createdDate!).toLocaleDateString()}
            </p>

            <div className="flex items-center mt-4 space-x-3">
                <Avatar>
                    {post.authorDTO!.profileImageUrl ? (
                        <AvatarImage
                            src={
                                post.authorDTO!.profileImageUrl &&
                                post.authorDTO!.profileImageUrl !== "/default-profile.png"
                                    ? process.env.NEXT_PUBLIC_BASE_URL +
                                    post.authorDTO!.profileImageUrl
                                    : "/default-profile.png"
                            }
                            alt={post.authorDTO!.nickname}
                        />
                    ) : (
                        <AvatarFallback>{post.authorDTO!.nickname![0]}</AvatarFallback>
                    )}
                </Avatar>
                <div>
                    <Link href={"/member/" + post.authorDTO?.memberId}>
                        <p className="font-semibold">{post.authorDTO!.nickname}</p>
                    </Link>
                </div>
                <Button variant="outline" onClick={() => handleFollowToggle(post.authorDTO?.memberId!)}>팔로우</Button>
            </div>

            {post.placeDTO && (
                <div className="mt-4 p-2 border border-black rounded-lg text-center">
                    <p className="font-semibold text-sm">{post.placeDTO.placeName}</p>
                </div>
            )}
            {/* 이미지 Carousel */}
            {post.imageUrls!.length > 0 && (
                <Carousel className="mt-2 mx-auto w-1/2 h-auto p-3">
                    <CarouselContent>
                        {post.imageUrls!.map((img, index) => (
                            <CarouselItem
                                key={index}
                                className="flex justify-center items-center max-w-[500px] mx-auto"
                                onClick={(e) => {
                                    e.stopPropagation();
                                    setActiveIndices(index);
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
                    <CarouselPrevious/>
                    <CarouselNext/>

                    {/* 인디케이터 */}
                    <div className="flex justify-center space-x-2 mt-2">
                        {post.imageUrls!.map((_, index) => (
                            <span
                                key={index}
                                className={`h-2 w-2 rounded-full ${
                                    index === (activeIndices || 0)
                                        ? "bg-blue-500"
                                        : "bg-gray-400"
                                }`}
                            ></span>
                        ))}
                    </div>
                </Carousel>
            )}
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
