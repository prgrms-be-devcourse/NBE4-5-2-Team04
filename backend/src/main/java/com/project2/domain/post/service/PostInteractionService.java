package com.project2.domain.post.service;

//import com.project2.domain.member.entity.Member;
//import com.project2.domain.post.dto.LikeResponseDTO;
//import com.project2.domain.post.entity.Likes;
//import com.project2.domain.post.entity.Post;
//import com.project2.domain.post.entity.Scrap;
//import com.project2.domain.post.repository.LikesRepository;
//import com.project2.domain.post.repository.PostRepository;
//import com.project2.domain.post.repository.ScrapRepository;
//import com.project2.global.dto.RsData;
//import com.project2.global.security.Rq;
//import lombok.RequiredArgsConstructor;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.Pageable;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//@Service
//@RequiredArgsConstructor
//public class PostInteractionService {
//
//    private final LikesRepository likesRepository;
//    private final ScrapRepository scrapRepository;
//    private final PostRepository postRepository;
//    private final Rq rq;
//
//    @Transactional
//    public RsData<LikeResponseDTO> toggleLikes(Long postId) {
//        Member actor = rq.getActor();
//        if (likesRepository.toggleLikeIfExists(postId, actor.getId()) > 0) {
//            return new RsData<>("200", "좋아요가 취소되었습니다.", likesRepository.getLikeStatus(postId, actor.getId()));
//        }
//
//        likesRepository.save(Likes.builder()
//                .post(Post.builder().id(postId).build())
//                .member(actor)
//                .build());
//
//        return new RsData<>("200", "좋아요가 추가되었습니다.", likesRepository.getLikeStatus(postId, actor.getId()));
//    }
//
//    @Transactional
//    public RsData<String> toggleScrap(Long postId) {
//        Member actor = rq.getActor();
//        if (scrapRepository.toggleScrapIfExists(postId, actor.getId()) > 0) {
//            return new RsData<>("200", "스크랩이 취소되었습니다.");
//        }
//
//        Post post = postRepository.getReferenceById(postId);
//        scrapRepository.save(new Scrap(null, post, actor));
//        return new RsData<>("200", "스크랩이 추가되었습니다.");
//    }
//
//    @Transactional(readOnly = true)
//    public Page<PostListResponseDTO> getScrappedPosts(Pageable pageable) {
//        Member actor = rq.getActor();
//        return scrapRepository.findScrappedPostsByMember(actor.getId(), pageable);
//    }
//}