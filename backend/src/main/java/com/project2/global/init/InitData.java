package com.project2.global.init;

import org.springframework.stereotype.Component;

import com.project2.domain.member.repository.MemberRepository;
import com.project2.domain.place.repository.PlaceRepository;
import com.project2.domain.post.repository.CommentRepository;
import com.project2.domain.post.repository.LikesRepository;
import com.project2.domain.post.repository.PostImageRepository;
import com.project2.domain.post.repository.PostRepository;
import com.project2.domain.post.repository.ScrapRepository;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class InitData {

	private final MemberRepository memberRepository;
	private final PlaceRepository placeRepository;
	private final PostRepository postRepository;
	private final PostImageRepository postImageRepository;
	private final LikesRepository likesRepository;
	private final ScrapRepository scrapRepository;
	private final CommentRepository commentRepository;

	@PostConstruct
	public void init() {
		// if (memberRepository.count() > 0) {
		// 	System.out.println("초기 데이터가 이미 존재하여 삽입하지 않음.");
		// 	return;
		// }
		//
		// Member naverUser = new Member();
		// naverUser.setEmail("test_naver@example.com");
		// naverUser.setNickname("네이버유저");
		// naverUser.setProfileImageUrl(null);
		// naverUser.setProvider(Provider.NAVER);
		// memberRepository.saveAndFlush(naverUser);
		//
		// Place place = new Place();
		// place.setId(1L);
		// place.setName("서울 명소");
		// place.setLatitude(37.5665);
		// place.setLongitude(126.9780);
		// place.setRegion(Region.SEOUL);
		// place.setCategory(Category.AT4);
		// placeRepository.saveAndFlush(place);
		//
		// Post post = new Post();
		// post.setTitle("서울 여행 추천 명소!");
		// post.setContent("서울에서 가볼 만한 곳을 추천합니다.");
		// post.setMember(naverUser);
		// post.setPlace(place);
		// postRepository.saveAndFlush(post);
		//
		// PostImage image = new PostImage();
		// image.setImageUrl("https://placehold.co/500x300");
		// image.setPost(post);
		// postImageRepository.saveAndFlush(image);
		//
		// Likes like = new Likes();
		// like.setMember(naverUser);
		// like.setPost(post);
		// likesRepository.saveAndFlush(like);
		//
		// Scrap scrap = new Scrap();
		// scrap.setMember(naverUser);
		// scrap.setPost(post);
		// scrapRepository.saveAndFlush(scrap);
		//
		// Comment comment = new Comment();
		// comment.setContent("정말 좋은 추천이네요!");
		// comment.setPost(post);
		// comment.setMember(naverUser);
		// comment.setDepth(0);
		// commentRepository.saveAndFlush(comment);
	}
}
