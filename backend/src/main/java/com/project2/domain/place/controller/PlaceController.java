package com.project2.domain.place.controller;

import com.project2.domain.place.dto.PlacePostResponseDTO;
import com.project2.domain.place.service.PlaceService;
import com.project2.global.dto.RsData;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class PlaceController {
    private final PlaceService placeService;

    // 특정 장소를 태그한 게시물 목록
    @GetMapping("/places/{placeId}/posts")
    public RsData<PlacePostResponseDTO> getPostsByPlace(
            @PathVariable Long placeId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page,size);
        return placeService.getPostByPlace(placeId,pageable);
    }
}

