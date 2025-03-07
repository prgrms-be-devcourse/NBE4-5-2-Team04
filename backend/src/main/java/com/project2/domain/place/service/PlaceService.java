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
    public RsData<PlacePostResponseDTO> getPostByPlace(Long placeId, Pageable pageable) {
        // 장소 정보 조회
        Place place = placeRepository.findById(placeId)
                .orElseThrow(() -> new ServiceException("404", "해당 장소가 존재하지 않습니다."));

        // 해당 장소의 게시글 조회
        Page<Post> postPage = postRepository.findByPlaceId(placeId, pageable);

        if (postPage.getTotalElements() == 0) {
            throw new ServiceException("204", "해당 장소에 게시물이 없습니다.");
        }

        // 응답 데이터 생성
        PlacePostResponseDTO response = buildPlacePostResponse(place, postPage);

        return new RsData<>("200", "장소별 게시글 목록을 성공적으로 조회하였습니다.", response);
    }

    private PlacePostResponseDTO buildPlacePostResponse(Place place, Page<Post> postPage) {
        return PlacePostResponseDTO.builder()
                .placeInfo(
                        PlaceInfoDTO.builder()
                                .placeId(place.getId())
                                .name(place.getName())
                                .latitude(place.getLatitude())
                                .longitude(place.getLongitude())
                                .category(place.getCategory())
                                .region(place.getRegion())
                                .postCount((int) postPage.getTotalElements())
                                .build()
                )
                .posts(
                        postPage.getContent().stream()
                                .map(post -> PostSummaryDTO.builder()
                                        .postId(post.getId())
                                        .title(post.getTitle())
                                        .imageUrl("")   // imageUrl 임시
                                        .likeCount(0)   // 좋아요 개수 임시
                                        .commentCount(0)// 댓글 개수 임시
                                        .createdAt(post.getCreatedDate().toString())
                                        .author(
                                                PostSummaryDTO.AuthorInfo.builder()
                                                        .userId(post.getMember().getId())
                                                        .nickname(post.getMember().getNickname())
                                                        .profileImageUrl(post.getMember().getProfileImageUrl())
                                                        .build()
                                        )
                                        .build())
                                .toList()
                )
                .pageNumber(postPage.getNumber())
                .pageSize(postPage.getSize())
                .totalPages(postPage.getTotalPages())
                .totalElements(postPage.getTotalElements())
                .build();
    }
}
