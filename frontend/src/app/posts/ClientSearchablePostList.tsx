"use client";

import {useEffect, useState} from "react";
import PostList from "@/components/posts/results/ClientPostList";
import {Input} from "@/components/ui/input";
import {Button} from "@/components/ui/button";
import {Select, SelectContent, SelectItem, SelectTrigger, SelectValue} from "@/components/ui/select";
import {categories} from "@/enums/CategoryEnum";
import {regions} from "@/enums/RegionEnum";


export default function SearchablePostList({queryKey, apiEndpoint}: { queryKey: string; apiEndpoint: string }) {
    const [placeName, setPlaceName] = useState<string | null>(null);
    const [category, setCategory] = useState<string | null>(null);
    const [region, setRegion] = useState<string | null>(null);
    const [searchParams, setSearchParams] = useState<{
        placeName: string | null;
        category: string | null;
        region: string | null
    }>({
        placeName: null,
        category: null,
        region: null,
    });

    const handleSearch = () => {
        setSearchParams({
            placeName: placeName || null,
            category: category || null,
            region: region || null,
        });
    };

    useEffect(() => {
        console.log("Updated searchParams:", searchParams);
    }, [searchParams]);


    return (
        <div className="max-w-2xl mx-auto space-y-4">
            <div className="flex flex-col space-y-2 mb-4">
                <Input
                    type="text"
                    placeholder="장소명"
                    value={placeName || ""}
                    onChange={(e) => setPlaceName(e.target.value || null)}
                />

                <Select onValueChange={setCategory} value={category || ""}>
                    <SelectTrigger>
                        <SelectValue placeholder="카테고리 선택"/>
                    </SelectTrigger>
                    <SelectContent>
                        {categories.map((cat) => (
                            <SelectItem key={cat.code} value={cat.code}>
                                {cat.name}
                            </SelectItem>
                        ))}
                    </SelectContent>
                </Select>

                <Select onValueChange={setRegion} value={region || ""}>
                    <SelectTrigger>
                        <SelectValue placeholder="지역 선택"/>
                    </SelectTrigger>
                    <SelectContent>
                        {regions.map((reg) => (
                            <SelectItem key={reg.code} value={reg.code}>
                                {reg.name}
                            </SelectItem>
                        ))}
                    </SelectContent>
                </Select>

                <Button onClick={handleSearch}>검색</Button>
            </div>

            <PostList
                key={JSON.stringify(searchParams)}
                queryKey={queryKey}
                apiEndpoint={apiEndpoint}
                placeName={searchParams.placeName}
                category={searchParams.category}
                region={searchParams.region}
            />
        </div>
    );
}