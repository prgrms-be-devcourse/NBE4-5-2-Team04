package com.project2.domain.post.service;

import com.project2.domain.place.entity.Place;
import com.project2.domain.place.enums.Category;
import com.project2.domain.place.enums.Region;
import com.project2.domain.place.repository.PlaceRepository;
import com.project2.domain.post.dto.PostRequestDTO;
import com.project2.domain.post.entity.Post;
import com.project2.domain.post.repository.PostRepository;
import com.project2.domain.member.entity.Member;
import com.project2.domain.member.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest  // ✅ Spring Boot 환경에서 실행 (MySQL 연결됨)
@Transactional   // ✅ 테스트 후 데이터 자동 롤백 (데이터 유지 X)
class PostServiceTest {

	@Autowired
	private PostRepository postRepository;

	@Autowired
	private PlaceRepository placeRepository;

	@Autowired
	private MemberRepository memberRepository;

	@Autowired
	private PostService postService;

	private Member testMember;

	@BeforeEach
	void setUp() {
		// ✅ 테스트할 멤버 저장
		testMember = Member.builder()
				.id(1L)
				.nickname("testUser")
				.build();
		memberRepository.save(testMember);
	}

	@Test
	void testCreatePost_WhenPlaceExists() throws IOException {
		// ✅ 1️⃣ 장소가 이미 DB에 존재하는 경우
		Place existingPlace = Place.builder()
				.id(100L)
				.name("서울 한강공원")
				.latitude(37.5326)
				.longitude(127.0246)
				.region(Region.SEOUL)
				.category(Category.PO3)
				.build();
		placeRepository.save(existingPlace);

		PostRequestDTO request = new PostRequestDTO(
				"서울 한강공원 방문 후기",
				"날씨가 좋아서 한강에 다녀왔어요.",
				100L,
				"서울 한강공원",
				"37.5326",
				"127.0246",
				"서울특별시",
				"공공기관",
				1L,
				List.of()
		);

		Long postId = postService.createPost(request);

		// ✅ 예상: 기존 장소를 사용하므로, 새로 저장되지 않아야 함
		Optional<Place> retrievedPlace = placeRepository.findById(100L);
		assertTrue(retrievedPlace.isPresent()); // ✅ 장소가 존재해야 함

		Optional<Post> savedPost = postRepository.findById(postId);
		assertTrue(savedPost.isPresent()); // ✅ 게시물이 저장되어야 함

		assertEquals(savedPost.get().getPlace().getId(), existingPlace.getId());
	}

	@Test
	void testCreatePost_WhenPlaceDoesNotExist() throws IOException {
		// ✅ 2️⃣ 장소가 DB에 없는 경우 (새로운 장소 저장)
		Long newPlaceId = 200L;
		PostRequestDTO request = new PostRequestDTO(
				"부산 광안리 방문 후기",
				"광안대교 야경이 너무 예뻤어요.",
				newPlaceId,
				"부산 광안리",
				"35.1571",
				"129.1597",
				"부산광역시",
				"관광명소",
				1L,
				List.of()
		);

		Long postId = postService.createPost(request);

		// ✅ 예상: 새로운 장소가 저장되어야 함
		Optional<Place> savedPlace = placeRepository.findById(newPlaceId);
		assertTrue(savedPlace.isPresent()); // ✅ 새로운 장소가 저장됨

		Optional<Post> savedPost = postRepository.findById(postId);
		assertTrue(savedPost.isPresent()); // ✅ 게시물이 저장되어야 함

		assertEquals(savedPost.get().getPlace().getId(), savedPlace.get().getId());
	}
}