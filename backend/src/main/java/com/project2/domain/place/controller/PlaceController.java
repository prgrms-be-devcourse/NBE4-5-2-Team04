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

}

