"use client";

import { useEffect, useRef, useState } from "react";
import { useRouter } from "next/navigation";
import { Input } from "@/components/ui/input";
import { Button } from "@/components/ui/button";
import { loadKakaoMap } from "@/lib/kakaoMapLoader";

export default function PlacePostPage() {
  const mapRef = useRef<HTMLDivElement>(null);
  const [mapLoaded, setMapLoaded] = useState(false);
  const [searchQuery, setSearchQuery] = useState("");
  const [places, setPlaces] = useState<
    kakao.maps.services.PlacesSearchResultItem[]
  >([]);
  const router = useRouter();

  useEffect(() => {
    loadKakaoMap(() => {
      setMapLoaded(true);
    });
  }, []);

  useEffect(() => {
    if (!mapLoaded || !window.kakao?.maps) return;
    window.kakao.maps.load(() => {
      new window.kakao.maps.Map(mapRef.current, {
        center: new window.kakao.maps.LatLng(37.5665, 126.978),
        level: 4,
      });
    });
  }, [mapLoaded]);

  const handleSearch = () => {
    if (!window.kakao?.maps?.services) return;

    const ps = new window.kakao.maps.services.Places();

    ps.keywordSearch(
      searchQuery,
      (
        data: kakao.maps.services.PlacesSearchResult,
        status: kakao.maps.services.Status
      ) => {
        if (status === window.kakao.maps.services.Status.OK) {
          setPlaces(data);
        } else {
          setPlaces([]);
        }
      }
    );
  };

  const handleSelectPlace = (
    place: kakao.maps.services.PlacesSearchResultItem
  ) => {
    router.push(`/posts?placeName=${encodeURIComponent(place.place_name)}`);
  };

  return (
    <div className="w-full h-screen flex flex-col">
      <div className="p-4 flex gap-2">
        <Input
          placeholder="장소 검색"
          value={searchQuery}
          onChange={(e) => setSearchQuery(e.target.value)}
        />
        <Button
          onClick={handleSearch}
          className="bg-blue-600 text-white hover:bg-blue-700"
        >
          검색
        </Button>
      </div>
      <div ref={mapRef} className="flex-1 border" />
      <div className="max-h-60 overflow-auto p-4">
        {places.map((place) => (
          <div
            key={place.id}
            className="p-2 border-b cursor-pointer hover:bg-gray-100"
            onClick={() => handleSelectPlace(place)}
          >
            <p className="font-semibold">{place.place_name}</p>
            <p className="text-sm text-gray-600">{place.address_name}</p>
            <p className="text-xs text-gray-500">
              {place.category_group_name || "기타"}
            </p>
          </div>
        ))}
      </div>
    </div>
  );
}
