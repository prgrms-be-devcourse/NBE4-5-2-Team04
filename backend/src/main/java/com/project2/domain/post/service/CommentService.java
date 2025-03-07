package com.project2.domain.post.service;

import com.project2.domain.member.entity.Member;
import com.project2.domain.post.dto.comment.CommentRequestDTO;
import com.project2.domain.post.dto.comment.CommentResponseDTO;
import com.project2.domain.post.dto.comment.ListCommentResponseDTO;
import com.project2.domain.post.entity.Comment;
import com.project2.domain.post.entity.Post;
import com.project2.domain.post.mapper.CommentMapper;
import com.project2.domain.post.repository.CommentRepository;
import com.project2.domain.post.repository.PostRepository;
import com.project2.global.dto.Empty;
import com.project2.global.dto.RsData;
import com.project2.global.exception.ServiceException;
import com.project2.global.security.Rq;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final CommentMapper commentMapper;
    private final Rq rq;

    @Transactional
    public RsData<CommentResponseDTO> createComment(Long postId, CommentRequestDTO request) {
        Member actor = rq.getActor();
        Post post = postRepository.getReferenceById(postId);

        Comment parentComment = Optional.ofNullable(request.getParentId())
                .map(commentRepository::findById)
                .flatMap(Function.identity())
                .orElse(null);

        if (parentComment != null && parentComment.getDepth() >= 1) {
            throw new ServiceException("400", "대대댓글은 허용되지 않습니다.");
        }
        Comment comment = commentRepository.save(commentMapper.toEntity(request, post, actor, parentComment));
        return new RsData<>("200", "댓글이 성공적으로 작성되었습니다.", commentMapper.toResponseDTO(comment));
    } // 댓글 작성

    @Transactional(readOnly = true)
    public RsData<List<ListCommentResponseDTO>> getComments(Long postId) {
        List<ListCommentResponseDTO> flatComments = commentRepository.findByPostIdWithParentId(postId);

        Map<Long, List<ListCommentResponseDTO>> childComments = flatComments.stream()
                .filter(comment -> comment.getParentId() != null)
                .collect(Collectors.groupingBy(ListCommentResponseDTO::getParentId));

        List<ListCommentResponseDTO> rootComments = flatComments.stream()
                .peek(comment -> comment.getChildren().addAll(childComments.getOrDefault(comment.getId(), new ArrayList<>())))
                .filter(comment -> comment.getParentId() == null)
                .collect(Collectors.toList());

        return new RsData<>("200", "댓글 목록 조회 성공", rootComments);
    } // 댓글 목록 조회

    @Transactional
    public RsData<CommentResponseDTO> updateComment(Long commentId, CommentRequestDTO request) {
        Member actor = rq.getActor();
        Comment comment = commentRepository.getReferenceById(commentId);

        if (!comment.getMember().equals(actor)) {
            throw new ServiceException("403", "댓글 수정 권한이 없습니다.");
        }
        comment.updateContent(request.getContent());
        return new RsData<>("200", "댓글이 성공적으로 수정되었습니다.", commentMapper.toResponseDTO(comment));
    } // 댓글 수정

    @Transactional
    public RsData<Empty> deleteComment(Long commentId) {
        Member actor = rq.getActor();
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ServiceException("404", "존재하지 않는 댓글입니다."));

        if (!comment.getMember().equals(actor)) {
            throw new ServiceException("403", "댓글 삭제 권한이 없습니다.");
        }
        commentRepository.delete(comment);
        return new RsData<>("200", "댓글이 성공적으로 삭제되었습니다.");
    } // 댓글 삭제
}