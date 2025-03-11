"use client";

import {useState} from "react";
import {Button} from "@/components/ui/button";
import {Avatar, AvatarFallback, AvatarImage} from "@/components/ui/avatar";
import {components} from "@/lib/backend/schema";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {faBookmark, faHeart} from "@fortawesome/free-solid-svg-icons";
import {Carousel, CarouselContent, CarouselItem} from "@/components/ui/carousel";
import Image from "next/image";
import {client} from "@/lib/backend/client";

export default function ClientPostDetail({post}: { post: components["schemas"]["PostDetailResponseDTO"] }) {
    const [isLiked, setIsLiked] = useState(post.isLiked);
    const [likeCount, setLikeCount] = useState(post.likeCount);
    const [isScrapped, setIsScrapped] = useState(post.isScrapped);
    const [scrapCount, setScrapCount] = useState(post.scrapCount);
    const [comment, setComment] = useState("");

    const handleLike = async () => {
        setIsLiked(!isLiked);
        setLikeCount(isLiked ? likeCount - 1 : likeCount + 1);
        await client.POST(`/api/posts/${post.id}/likes`, {credentials: "include"});
    };

    const handleScrap = async () => {
        setIsScrapped(!isScrapped);
        setScrapCount(isScrapped ? scrapCount - 1 : scrapCount + 1);
        await client.POST(`/api/posts/${post.id}/scrap`, {credentials: "include"});
    };

    const handleCommentSubmit = async () => {
        if (!comment.trim()) return;
        console.log("댓글 작성:", comment);
        await client.POST(`/api/posts/${post.id}/comments`, {
            body: {content: comment},
            credentials: "include",
        });
        setComment(""); // 댓글 입력 후 초기화
    };

    return (
        <div className="max-w-2xl mx-auto py-10">
            <h1 className="text-2xl font-bold">{post.title}</h1>
            <p className="text-sm text-gray-500">
                {new Date(post.createdDate!).toLocaleDateString()}
            </p>

            {/* 작성자 정보 */}
            <div className="flex items-center mt-4 space-x-3">
                <Avatar>
                    <AvatarImage src={post.authorDTO!.profileImageUrl ?? ""} alt={post.authorDTO!.nickname}/>
                    <AvatarFallback>{post.authorDTO!.nickname!.charAt(0)}</AvatarFallback>
                </Avatar>
                <div>
                    <p className="font-semibold">{post.authorDTO!.nickname}</p>
                </div>
                <Button variant="outline">팔로우</Button>
            </div>

            {/* 장소 정보 */}
            <div className="border p-4 mt-4 rounded-md">
                <p className="font-semibold">{post.placeDTO!.placeName}</p>
            </div>

            {/* 이미지 */}
            {post.imageUrls!.length > 0 && (
                <div className="mt-4">
                    <Carousel>
                        <CarouselContent>
                            {post.imageUrls!.map((imageUrl, index) => (
                                <CarouselItem key={index}>
                                    <Image
                                        src={process.env.NEXT_PUBLIC_BASE_URL + imageUrl}
                                        alt={`Post Image ${index + 1}`}
                                        width={600}
                                        height={400}
                                        className="w-full max-w-md rounded-lg"
                                        unoptimized
                                    />
                                </CarouselItem>
                            ))}
                        </CarouselContent>
                    </Carousel>
                </div>
            )}

            {/* 게시글 내용 */}
            <p className="mt-4">{post.content}</p>

            {/* 좋아요 & 스크랩 & 댓글 수 */}
            <div className="flex mt-4 space-x-6 text-gray-600">
                <button onClick={handleLike} className={isLiked ? "text-yellow-500" : "text-gray-500"}>
                    <FontAwesomeIcon
                        icon={faHeart}
                        className={`w-5 h-5 ${isLiked ? "text-red-500" : "text-gray-400"}`}
                    />{" "}
                    좋아요 {likeCount}
                </button>
                <button onClick={handleScrap} className={isScrapped ? "text-yellow-500" : "text-gray-500"}>
                    <FontAwesomeIcon
                        icon={faBookmark}
                        className={`w-5 h-5 ${isScrapped ? "text-blue-500" : "text-gray-400"}`}
                    />{" "}
                    스크랩 {scrapCount}
                </button>
                <p>댓글 8</p>
            </div>

            {/* 댓글 입력 */}
            <div className="mt-6 flex space-x-2">
                <input
                    type="text"
                    placeholder="댓글 입력"
                    className="border w-full p-2 rounded-md"
                    value={comment}
                    onChange={(e) => setComment(e.target.value)}
                />
                <Button onClick={handleCommentSubmit}>작성</Button>
            </div>

            {/* 댓글 목록 (예제 데이터) */}
            {/*TODO 댓글 조회하는거 넣기*/}
            <div className="mt-4 space-y-4">
                <div className="flex items-center justify-between">
                    <p className="font-semibold">User2</p>
                    <div className="space-x-2">
                        <Button variant="ghost">수정</Button>
                        <Button variant="destructive">삭제</Button>
                    </div>
                </div>
                <p>댓글 Comment</p>

                <div className="ml-6 flex items-center justify-between">
                    <p className="font-semibold">User3</p>
                    <div className="space-x-2">
                        <Button variant="ghost">수정</Button>
                        <Button variant="destructive">삭제</Button>
                    </div>
                </div>
                <p className="ml-6">댓글2 Comment</p>
            </div>
        </div>
    );
}