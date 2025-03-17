"use client";

import {useEffect, useState} from "react";
import {Input} from "@/components/ui/input";
import {Textarea} from "@/components/ui/textarea";
import {Button} from "@/components/ui/button";
import {Carousel, CarouselContent, CarouselItem, CarouselNext, CarouselPrevious} from "@/components/ui/carousel";
import {ImageIcon, Upload, XCircle} from "lucide-react";
import Image from "next/image";
import {clientFormData} from "@/lib/backend/client";
import {useRouter} from "next/navigation";
import PostPlacePicker from "@/components/map/PostPlacePicker";
import {Place} from "@/types/place";

export default function ClientPostForm({initialData, postId}: {
    initialData?: {
        title?: string | undefined;
        content?: string | undefined;
        placeId?: string | undefined;
        latitude?: number | undefined;
        longitude?: number | undefined;
        placeName?: string | undefined;
        category?: string | undefined;
        region?: string | undefined;
        images?: string[] | undefined;
    }
    postId: number,
}) {
    const [images, setImages] = useState<File[]>([]);
    const [title, setTitle] = useState(initialData?.title || "");
    const [content, setContent] = useState(initialData?.content || "");
    const [selectedLocation, setSelectedLocation] = useState<Place | null>(null);
    const [showMap, setShowMap] = useState(false);
    const [activeIndices, setActiveIndices] = useState<number>(0);
    const router = useRouter();
    const fetchImages = async () => {
        try {
            const files = await Promise.all(
                initialData!.images!.map(async (url, index) => {
                    const response = await fetch(`${process.env.NEXT_PUBLIC_BASE_URL}${url}`);
                    const blob = await response.blob();
                    return new File([blob], `image-${index}.jpg`, {type: blob.type});
                })
            );
            setImages(files);
        } catch (error) {
            console.error("이미지 변환 실패:", error);
        }
    }

    useEffect(() => {
        if (initialData) {
            const place: Place = {
                id: initialData.placeId!,
                name: initialData.placeName!,
                category: initialData.category!,
                city: initialData.region!,
                lat: initialData.latitude!,
                lng: initialData.longitude!,
            }
            setSelectedLocation(place);
            if (initialData?.images) {
                fetchImages();
            }
        }
    }, [initialData]);

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
        if (!title.trim()) {
            alert("제목을 입력해주세요.");
            return;
        }

        if (!content.trim()) {
            alert("내용을 입력해주세요.");
            return;
        }

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


        try {
            let response;

            if (initialData) {
                response = await clientFormData.PUT("/api/posts/{postId}", {
                    params: {path: {postId}},
                    body: formData,
                    credentials: "include",
                });
            } else {
                response = await clientFormData.POST("/api/posts", {
                    body: formData,
                    credentials: "include",
                });
            }

            if (response.data!.code !== '201') {
                alert(response.data!.msg);
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
                                onClick={(e) => {
                                    e.stopPropagation();
                                    setActiveIndices(index);
                                }}
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
                                    <XCircle size={20}/>
                                </button>
                            </CarouselItem>
                        ))}
                    </CarouselContent>
                    <CarouselPrevious/>
                    <CarouselNext/>
                    <div className="flex justify-center space-x-2 mt-2">
                        {images.map((_, index) => (
                            <span
                                key={index}
                                className={`h-2 w-2 rounded-full ${
                                    index === (activeIndices)
                                        ? "bg-blue-500"
                                        : "bg-gray-400"
                                }`}
                            ></span>
                        ))}
                    </div>
                </Carousel>
            ) : (
                <div className="w-full h-48 flex items-center justify-center rounded-lg">
                    <ImageIcon className="w-12 h-12 text-gray-500"/>
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
            {showMap && <PostPlacePicker onSelectLocation={setSelectedLocation} onClose={() => setShowMap(false)}/>}

            {/* 이미지 업로드 버튼 */}
            <label className="flex items-center gap-2 mt-2 cursor-pointer">
                <Upload className="w-6 h-6 text-gray-700"/>
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
