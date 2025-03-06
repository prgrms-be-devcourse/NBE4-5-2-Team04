package com.project2.domain.place.dto;

import com.project2.domain.place.enums.Category;
import com.project2.domain.place.enums.Region;

public class PlaceInfoDTO {
    private Long placeId;
    private String name;
    private Double latitude;
    private Double longitude;
    private Category category;
    private Region region;
    private int postCount;
}
