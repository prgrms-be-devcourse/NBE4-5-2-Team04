"use client";

import { useState } from "react";
import { Input } from "@/components/ui/input";
import { Textarea } from "@/components/ui/textarea";
import { Button } from "@/components/ui/button";
import { Carousel, CarouselContent, CarouselItem, CarouselNext, CarouselPrevious } from "@/components/ui/carousel";
import { ImageIcon, Upload, XCircle } from "lucide-react";
import Image from "next/image";
import { clientFormData } from "@/lib/backend/client";
import { useRouter } from "next/navigation";
import PostPlacePicker from "@/components/map/PostPlacePicker";
import { Place } from "@/types/place";

export default function ClientPostForm() {
    const [images, setImages] = useState<File[]>([]);
    const [title, setTitle] = useState("");
    const [content, setContent] = useState("");
    const [selectedLocation, setSelectedLocation] = useState<Place | null>(null);
    const [showMap, setShowMap] = useState(false);
    const router = useRouter();

    const handleImageUpload = (event: React.ChangeEvent<HTMLInputElement>) => {
        const files = event.target.files;
        if (files) {
            setImages((prev) => [...prev, ...Array.from(files)]);
        }
    };

    const handleRemoveImage = (index: number) => {
        setImages((prev) => prev.filter((_, i) => i !== index));
    };

    const handleSubmit = async () => {
        const formData = new FormData();
        formData.append("title", title);
        formData.append("content", content);
        formData.append("memberId", "1");
    
        if (selectedLocation) {
            formData.append("placeId", selectedLocation.id.toString());  
            formData.append("placeName", selectedLocation.name);
            formData.append("latitude", selectedLocation.lat.toString()); 
            formData.append("longitude", selectedLocation.lng.toString()); 
            formData.append("region", selectedLocation.city); 
            formData.append("category", selectedLocation.category);
        }
    
        images.forEach((image) => {
            formData.append("images", image);
        });
    
        console.log("FormData 확인:");
        for (let pair of formData.entries()) {
            console.log(`🔹 ${pair[0]}:`, pair[1]);
        }
    
        try {
            const response = await clientFormData.POST("/api/posts", {
                body: formData,
                credentials: "include",
            });
    
            if (response.data!.code !== '201') {
                alert(response.data!.msg);
            } else {
                alert("작성 완료!");
                router.push(`/posts/${response.data!.data}`);
            }
        } catch (error) {
            console.error("업로드 실패:", error);
            alert("작성 실패!");
        }
    };

    return (
        <div className="max-w-lg mx-auto p-4 border rounded-lg">
            {images.length > 0 ? (
                <Carousel className="w-full h-48 mb-4">
                    <CarouselContent>
                        {images.map((file, index) => (
                            <CarouselItem
                                key={index}
                                className="relative flex justify-center items-center"
                            >
                                <Image
                                    src={URL.createObjectURL(file)}
                                    alt={`uploaded-${index}`}
                                    className="w-full h-48 object-cover rounded-lg"
                                    width={500}
                                    height={500}
                                />
                                <button
                                    className="absolute top-2 right-2 bg-black/50 rounded-full p-1 hover:bg-black"
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
                <div className="w-full h-48 flex items-center justify-center rounded-lg">
                    <ImageIcon className="w-12 h-12 text-gray-500" />
                </div>
            )}

            {/* 제목 입력 */}
            <Input
                placeholder="제목"
                className="mt-2"
                value={title}
                onChange={(e) => setTitle(e.target.value)}
            />

            {/* 내용 입력 */}
            <Textarea
                placeholder="내용"
                className="mt-2"
                value={content}
                onChange={(e) => setContent(e.target.value)}
            />

            {/* 위치 추가 버튼 */}
            <Button onClick={() => setShowMap(true)} className="mt-2 bg-green-600 text-white hover:bg-green-700">
                위치 추가
            </Button>

            {/* 선택한 위치 표시 */}
            {selectedLocation && (
                <p className="mt-2 text-sm text-gray-600">
                    {selectedLocation.name} ({selectedLocation.city}) - {selectedLocation.category}
                </p>
            )}

            {/* 카카오맵 모달 */}
            {showMap && <PostPlacePicker onSelectLocation={setSelectedLocation} onClose={() => setShowMap(false)} />}

            {/* 이미지 업로드 버튼 */}
            <label className="flex items-center gap-2 mt-2 cursor-pointer">
                <Upload className="w-6 h-6 text-gray-700" />
                <span className="text-gray-700">이미지 업로드</span>
                <input
                    type="file"
                    multiple
                    className="hidden"
                    onChange={handleImageUpload}
                    accept="image/*"
                />
            </label>

            {/* 작성 버튼 */}
            <Button
                onClick={handleSubmit}
                className="w-full mt-4 bg-blue-600 text-white hover:bg-blue-700"
            >
                작성
            </Button>
        </div>
    );
}