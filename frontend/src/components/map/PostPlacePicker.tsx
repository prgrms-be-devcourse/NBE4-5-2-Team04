"use client";

import { useEffect, useRef, useState } from "react";
import { Input } from "@/components/ui/input";
import { Button } from "@/components/ui/button";
import { Modal } from "@/components/ui/modal";
import { loadKakaoMap } from "@/lib/kakaoMapLoader";
import { Place } from "@/types/place";

interface PostLocationPickerProps {
    onSelectLocation: (place: Place) => void;
    onClose: () => void;
}

export default function PostLocationPicker({ onSelectLocation, onClose }: PostLocationPickerProps) {
    const mapRef = useRef<HTMLDivElement>(null);
    const [mapLoaded, setMapLoaded] = useState(false);
    const [searchQuery, setSearchQuery] = useState("");
    const [places, setPlaces] = useState<kakao.maps.services.PlacesSearchResultItem[]>([]);
    const [selectedPlace, setSelectedPlace] = useState<Place | null>(null);

    useEffect(() => {
        loadKakaoMap(() => {
            console.log("카카오맵 로드 완료 - 상태 업데이트");
            setMapLoaded(true);
        });
    }, []);

    useEffect(() => {
        if (!mapLoaded) {
            console.warn("카카오맵 로드되지 않음");
            return;
        }
        if (!window.kakao || !window.kakao.maps) {
            console.error("window.kakao 또는 window.kakao.maps 없음");
            return;
        }
        window.kakao.maps.load(() => {
            const map = new window.kakao.maps.Map(mapRef.current, {
                center: new window.kakao.maps.LatLng(37.5665, 126.9780),
                level: 3,
            });
        });
    }, [mapLoaded]);

    const handleSearch = () => {
        if (!window.kakao?.maps?.services) {
            console.error("카카오맵 서비스가 로드되지 않음");
            return;
        }
    
        const ps = new window.kakao.maps.services.Places();
        ps.keywordSearch(
            searchQuery,
            (data: kakao.maps.services.PlacesSearchResult, status: kakao.maps.services.Status) => {
                console.log("검색 결과:", data, "상태:", status);
    
                if (status === window.kakao.maps.services.Status.OK) {
                    setPlaces(data);  
                } else {
                    setPlaces([]); 
                }
            }
        );
    };

    const handleSelectPlace = (place: kakao.maps.services.PlacesSearchResultItem) => {
        const placeData: Place = {
            id: place.id,
            name: place.place_name,
            lat: parseFloat(place.y),
            lng: parseFloat(place.x),
            city: place.address_name.split(" ")[0],
            category: place.category_group_name || "기타",
        };

        setSelectedPlace(placeData);
    };

    const handleConfirm = () => {
        if (selectedPlace) {
            onSelectLocation(selectedPlace);
            onClose();
        }
    };

    return (
        <Modal onClose={onClose} title="장소 검색">
            <div className="flex gap-2">
                <Input
                    placeholder="장소 검색"
                    value={searchQuery}
                    onChange={(e) => setSearchQuery(e.target.value)}
                />
                <Button onClick={handleSearch} className="bg-blue-600 text-white hover:bg-blue-700">
                    검색
                </Button>
            </div>

            <div className="mt-2 max-h-60 overflow-auto">
                {places.map((place) => (
                    <div
                        key={place.id}
                        className={`p-2 border-b cursor-pointer ${
                            selectedPlace?.id === place.id ? "bg-blue-100" : ""
                        }`}
                        onClick={() => handleSelectPlace(place)}
                    >
                        <p className="font-semibold">{place.place_name}</p>
                        <p className="text-sm text-gray-600">{place.address_name}</p>
                        <p className="text-xs text-gray-500">{place.category_group_name || "기타"}</p>
                    </div>
                ))}
            </div>

            {selectedPlace && (
                <div className="mt-4 p-2 border rounded-lg bg-gray-100">
                    <p className="font-semibold">{selectedPlace.name}</p>
                    <p className="text-sm text-gray-600">위도: {selectedPlace.lat}, 경도: {selectedPlace.lng}</p>
                    <p className="text-sm text-gray-600">시/도: {selectedPlace.city}</p>
                    <p className="text-sm text-gray-600">카테고리: {selectedPlace.category}</p>
                </div>
            )}

            <Button onClick={handleConfirm} className="mt-4 w-full bg-green-600 text-white hover:bg-green-700">
                선택 완료
            </Button>
            <div ref={mapRef} className="w-full h-96 mt-4 border rounded-lg" />
        </Modal>
    );
}