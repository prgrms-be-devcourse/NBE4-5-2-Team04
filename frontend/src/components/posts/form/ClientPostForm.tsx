"use client";

import { useState } from "react";
import { Input } from "@/components/ui/input";
import { Textarea } from "@/components/ui/textarea";
import { Button } from "@/components/ui/button";
import { Carousel, CarouselContent, CarouselItem, CarouselNext, CarouselPrevious } from "@/components/ui/carousel";
import { ImageIcon, Upload, XCircle } from "lucide-react";
import Image from "next/image";
import { clientFormData } from "@/lib/backend/client";

export default function ClientPostForm() {
    // ✅ images 를 File[]로 관리
    const [images, setImages] = useState<File[]>([]);
    const [title, setTitle] = useState("");
    const [content, setContent] = useState("");

    // ✅ 이미지 업로드 핸들러 (File[]로 저장)
    const handleImageUpload = (event: React.ChangeEvent<HTMLInputElement>) => {
        const files = event.target.files;
        if (files) {
            setImages((prev) => [...prev, ...Array.from(files)]);
        }
    };

    // ✅ 이미지 삭제 핸들러
    const handleRemoveImage = (index: number) => {
        setImages((prev) => prev.filter((_, i) => i !== index));
    };

    // 📝 서버로 데이터 전송
    const handleSubmit = async () => {
        const formData = new FormData();
        formData.append("title", title);
        formData.append("content", content);
        // TODO 위경도 바꿔줘야함
        formData.append("placeId", "1");
        formData.append("memberId", "1");

        images.forEach((image) => {
            formData.append("images", image);
        });

        try {
            const response = await clientFormData.POST('/api/posts', {
                formData,
            });

            console.log("업로드 성공:", response.data);
            alert("작성 완료!");
        } catch (error) {
            console.error("업로드 실패:", error);
            alert("작성 실패!");
        }
    };

    return (
        <div className="max-w-lg mx-auto p-4 border rounded-lg bg-gray-200">
            {/* ✅ 이미지 Carousel */}
            {images.length > 0 ? (
                <Carousel className="w-full h-48 mb-4">
                    <CarouselContent>
                        {images.map((file, index) => (
                            <CarouselItem key={index} className="relative flex justify-center items-center">
                                {/* 이미지 렌더링 시 URL 변환 */}
                                <Image
                                    src={URL.createObjectURL(file)}
                                    alt={`uploaded-${index}`}
                                    className="w-full h-48 object-cover rounded-lg"
                                    width={500} height={500}
                                />
                                {/* 삭제 버튼 */}
                                <button
                                    className="absolute top-2 right-2 bg-black/50 rounded-full p-1 text-white hover:bg-black"
                                    onClick={() => handleRemoveImage(index)}
                                >
                                    <XCircle size={20} />
                                </button>
                            </CarouselItem>
                        ))}
                    </CarouselContent>
                    <CarouselPrevious />
                    <CarouselNext />
                </Carousel>
            ) : (
                <div className="w-full h-48 bg-gray-300 flex items-center justify-center rounded-lg">
                    <ImageIcon className="w-12 h-12 text-gray-500" />
                </div>
            )}

            {/* 제목 입력 */}
            <Input placeholder="제목" className="mt-2" value={title} onChange={(e) => setTitle(e.target.value)} />

            {/* 내용 입력 */}
            <Textarea placeholder="내용" className="mt-2" value={content} onChange={(e) => setContent(e.target.value)} />

            {/* 이미지 업로드 버튼 */}
            <label className="flex items-center gap-2 mt-2 cursor-pointer">
                <Upload className="w-6 h-6 text-gray-700" />
                <span className="text-gray-700">이미지 업로드</span>
                <input type="file" multiple className="hidden" onChange={handleImageUpload} accept="image/*" />
            </label>

            {/* 작성 버튼 */}
            <Button onClick={handleSubmit} className="w-full mt-4 bg-blue-600 text-white hover:bg-blue-700">작성</Button>
        </div>
    );
}