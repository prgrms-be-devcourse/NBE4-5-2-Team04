package com.project2.domain.place.service;

import com.project2.domain.member.entity.Member;
import com.project2.domain.place.dto.PlacePostResponseDTO;
import com.project2.domain.place.entity.Place;
import com.project2.domain.place.enums.Category;
import com.project2.domain.place.enums.Region;
import com.project2.domain.place.repository.PlaceRepository;
import com.project2.domain.post.entity.Post;
import com.project2.domain.post.repository.PostRepository;
import com.project2.global.dto.RsData;
import com.project2.global.exception.ServiceException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PlaceServiceTest {

    @Mock private PlaceRepository placeRepository;
    @Mock private PostRepository postRepository;

    @InjectMocks private PlaceService placeService;

    private Place testPlace;
    private Post testPost;
    private Pageable pageable;

    @BeforeEach
    void setUp() {
        // ✅ 테스트용 Place 객체 생성
        testPlace = Place.builder()
                .id(123L)
                .name("서울 남산타워")
                .latitude(37.5512)
                .longitude(126.9882)
                .region(Region.SEOUL)
                .category(Category.AT4)
                .build();

        // 테스트용 Member
        Member testMember = Member.builder()
                .id(1L)
                .nickname("테스트 유저")
                .email("test@example.com")
                .profileImageUrl("https://example.com/profile.jpg")
                .build();

        // ✅ 테스트용 Post 객체 생성
        testPost = Post.builder()
                .id(1L)
                .title("남산타워 야경이 멋져요!")
                .content("남산타워에서 본 서울 야경 🌃")
                .place(testPlace)
                .member(testMember)
                .createdDate(LocalDateTime.now()) // ✅ createdDate 설정
                .build();



        pageable = PageRequest.of(0, 10); // ✅ 0페이지, 10개씩 조회
    }

    @Test
    @DisplayName("장소 ID로 게시물 목록 조회 성공")
    void getPostByPlace_Success() {
        // ✅ Mock 설정: 장소가 존재하고, 게시글도 존재하는 경우
        when(placeRepository.findById(testPlace.getId())).thenReturn(Optional.of(testPlace));
        when(postRepository.findByPlaceId(testPlace.getId(), pageable))
                .thenReturn(new PageImpl<>(List.of(testPost), pageable, 1));

        // ✅ 실제 서비스 메서드 호출
        RsData<PlacePostResponseDTO> response = placeService.getPostByPlace(testPlace.getId(), pageable);

        // ✅ 검증: 응답 데이터가 정상적으로 반환되었는지 확인
        assertThat(response.getCode()).isEqualTo("200");
        assertThat(response.getData()).isNotNull();
        assertThat(response.getData().getPlaceInfo().getPlaceId()).isEqualTo(testPlace.getId());
        assertThat(response.getData().getPosts()).hasSize(1);
        assertThat(response.getData().getPosts().get(0).getTitle()).isEqualTo(testPost.getTitle());

        // ✅ Mock이 예상대로 호출되었는지 검증
        verify(placeRepository, times(1)).findById(testPlace.getId());
        verify(postRepository, times(1)).findByPlaceId(testPlace.getId(), pageable);
    }

    @Test
    @DisplayName("장소 ID로 게시물 목록 조회 실패 - 장소 없음")
    void getPostByPlace_Fail_PlaceNotFound() {
        // ✅ Mock 설정: 장소가 존재하지 않는 경우
        when(placeRepository.findById(testPlace.getId())).thenReturn(Optional.empty());

        // ✅ 예외 발생 검증
        assertThatThrownBy(() -> placeService.getPostByPlace(testPlace.getId(), pageable))
                .isInstanceOf(ServiceException.class)
                .hasMessage("해당 장소가 존재하지 않습니다.");

        // ✅ placeRepository가 한 번 호출되었는지 검증 (postRepository는 호출되지 않아야 함)
        verify(placeRepository, times(1)).findById(testPlace.getId());
        verify(postRepository, never()).findByPlaceId(any(), any());
    }

    @Test
    @DisplayName("장소 ID로 게시물 목록 조회 실패 - 게시물 없음")
    void getPostByPlace_Fail_NoPosts() {
        // ✅ Mock 설정: 장소는 존재하지만, 게시글이 없는 경우
        when(placeRepository.findById(testPlace.getId())).thenReturn(Optional.of(testPlace));
        when(postRepository.findByPlaceId(testPlace.getId(), pageable))
                .thenReturn(Page.empty());

        // ✅ 예외 발생 검증 (204 No Content)
        assertThatThrownBy(() -> placeService.getPostByPlace(testPlace.getId(), pageable))
                .isInstanceOf(ServiceException.class)
                .hasMessage("해당 장소에 게시물이 없습니다.");

        // ✅ placeRepository와 postRepository가 각각 한 번씩 호출되었는지 검증
        verify(placeRepository, times(1)).findById(testPlace.getId());
        verify(postRepository, times(1)).findByPlaceId(testPlace.getId(), pageable);
    }
}