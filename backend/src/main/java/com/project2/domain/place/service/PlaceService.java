package com.project2.domain.place.service;

import com.project2.domain.place.dto.PlaceInfoDTO;
import com.project2.domain.place.dto.PlacePostResponseDTO;
import com.project2.domain.place.entity.Place;
import com.project2.domain.place.enums.Category;
import com.project2.domain.place.enums.Region;
import com.project2.domain.place.repository.PlaceRepository;
import com.project2.domain.post.dto.post.PostSummaryDTO;
import com.project2.domain.post.entity.Post;
import com.project2.domain.post.repository.PostRepository;
import com.project2.global.dto.RsData;
import com.project2.global.exception.ServiceException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PlaceService {

    private final PlaceRepository placeRepository;
    private final PostRepository postRepository;

    @Transactional
    public Place savePlace(Long placeId, String name, Double latitude, Double longitude, Region region, Category category) {

        // placeId가 null이면 예외 발생 방지
        if (placeId == null) {
            throw new IllegalArgumentException("placeId는 null일 수 없습니다.");
        }

        // 이미 존재하는 placeId라면 저장하지 않음
        return placeRepository.findById(placeId)
                .orElseGet(() -> {
                    Place newPlace = Place.builder()
                            .id(placeId)
                            .name(name)
                            .latitude(latitude)
                            .longitude(longitude)
                            .region(region)
                            .category(category)
                            .build();
                    return placeRepository.save(newPlace);
                });
    }

}
