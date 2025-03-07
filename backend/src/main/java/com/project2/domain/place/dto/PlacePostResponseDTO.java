package com.project2.domain.place.dto;

import com.project2.domain.post.dto.post.PostSummaryDTO;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class PlacePostResponseDTO {
    private PlaceInfoDTO placeInfo;
    private List<PostSummaryDTO> posts;
    private int pageNumber;
    private int pageSize;
    private int totalPages;
    private long totalElements;
}