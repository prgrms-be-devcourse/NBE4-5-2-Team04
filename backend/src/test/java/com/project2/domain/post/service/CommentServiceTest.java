package com.project2.domain.post.service;

import com.project2.domain.member.entity.Member;
import com.project2.domain.post.dto.comment.CommentRequestDTO;
import com.project2.domain.post.dto.comment.CommentResponseDTO;
import com.project2.domain.post.entity.Comment;
import com.project2.domain.post.entity.Post;
import com.project2.domain.post.mapper.CommentMapper;
import com.project2.domain.post.repository.CommentRepository;
import com.project2.domain.post.repository.PostRepository;
import com.project2.global.dto.Empty;
import com.project2.global.dto.RsData;
import com.project2.global.exception.ServiceException;
import com.project2.global.security.Rq;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

    @Mock private CommentRepository commentRepository;
    @Mock private PostRepository postRepository;
    @Mock private Rq rq;
    @Mock private CommentMapper commentMapper;

    @InjectMocks private CommentService commentService;

    private Member testUser;
    private Post testPost;
    private Comment testComment;
    private CommentRequestDTO requestDTO;

    @BeforeEach
    void setUp() {
        testUser = Member.builder().id(1L).email("test@example.com").nickname("TestUser").build();
        testPost = Post.builder().id(1L).build();
        testComment = Comment.builder()
                .id(1L)
                .content("Test Comment")
                .post(testPost)
                .member(testUser)
                .depth(0)
                .build();
        requestDTO = new CommentRequestDTO("Updated Comment", null);
    }

    @Test
    @DisplayName("댓글 작성 성공")
    void createComment_Success() {
        when(rq.getActor()).thenReturn(testUser);
        when(postRepository.getReferenceById(testPost.getId())).thenReturn(testPost);
        when(commentMapper.toEntity(any(), any(), any(), any())).thenReturn(testComment);
        when(commentRepository.save(any(Comment.class))).thenReturn(testComment);
        when(commentMapper.toResponseDTO(any())).thenReturn(new CommentResponseDTO(1L, "Test Comment", "TestUser"));

        RsData<CommentResponseDTO> response = commentService.createComment(testPost.getId(), requestDTO);

        assertThat(response.getCode()).isEqualTo("200");
        assertThat(response.getData()).isNotNull();
        assertThat(response.getData().getContent()).isEqualTo("Test Comment");

        verify(commentMapper, times(1)).toEntity(any(), any(), any(), any());
        verify(commentRepository, times(1)).save(any(Comment.class));
        verify(commentMapper, times(1)).toResponseDTO(any());
    }

    @Test
    @DisplayName("댓글 목록 조회 성공")
    void getComments_Success() {
        when(commentRepository.findByPostId(testPost.getId()))
                .thenReturn(List.of(new CommentResponseDTO(1L, "Test Comment", "TestUser")));

        RsData<List<CommentResponseDTO>> response = commentService.getComments(testPost.getId());

        assertThat(response.getCode()).isEqualTo("200");
        assertThat(response.getData()).hasSize(1);
        assertThat(response.getData().getFirst().getContent()).isEqualTo("Test Comment");
    }

    @Test
    @DisplayName("댓글 수정 성공")
    void updateComment_Success() {
        when(rq.getActor()).thenReturn(testUser);
        when(commentRepository.getReferenceById(testComment.getId())).thenReturn(testComment);
        when(commentMapper.toResponseDTO(any())).thenReturn(new CommentResponseDTO(1L, "Updated Comment", "TestUser"));

        RsData<CommentResponseDTO> response = commentService.updateComment(testComment.getId(), requestDTO);

        assertThat(response.getCode()).isEqualTo("200");
        assertThat(testComment.getContent()).isEqualTo(requestDTO.getContent());

        verify(commentMapper, times(1)).toResponseDTO(any());
    }

    @Test
    @DisplayName("댓글 수정 실패 - 권한 없음")
    void updateComment_Fail_NoPermission() {
        Member anotherUser = Member.builder().id(2L).build();
        when(rq.getActor()).thenReturn(anotherUser);
        when(commentRepository.getReferenceById(testComment.getId())).thenReturn(testComment);

        assertThatThrownBy(() -> commentService.updateComment(testComment.getId(), requestDTO))
                .isInstanceOf(ServiceException.class)
                .hasMessage("댓글 수정 권한이 없습니다.");
    }

    @Test
    @DisplayName("댓글 삭제 성공")
    void deleteComment_Success() {
        when(rq.getActor()).thenReturn(testUser);
        when(commentRepository.deleteByIdAndMemberId(testComment.getId(), testUser.getId()))
                .thenReturn(1);

        RsData<Empty> response = commentService.deleteComment(testComment.getId());

        assertThat(response.getCode()).isEqualTo("200");
        verify(commentRepository, times(1)).deleteByIdAndMemberId(testComment.getId(), testUser.getId());
    }

    @Test
    @DisplayName("댓글 삭제 실패 - 권한 없음")
    void deleteComment_Fail_NoPermission() {
        when(rq.getActor()).thenReturn(testUser);
        when(commentRepository.deleteByIdAndMemberId(testComment.getId(), testUser.getId()))
                .thenReturn(0); // ✅ 삭제 실패 (권한 없음)

        assertThatThrownBy(() -> commentService.deleteComment(testComment.getId()))
                .isInstanceOf(ServiceException.class)
                .hasMessage("댓글 삭제 권한이 없습니다.");

        verify(commentRepository, times(1)).deleteByIdAndMemberId(testComment.getId(), testUser.getId());
    }

    @Test
    @DisplayName("댓글 작성 실패 - 대대댓글 깊이 초과")
    void createComment_Fail_DepthLimitExceeded() {
        Comment.builder().id(2L).depth(1).build();
        when(rq.getActor()).thenReturn(testUser);
        when(postRepository.getReferenceById(testPost.getId())).thenReturn(testPost);
        when(commentMapper.toEntity(any(), any(), any(), any())).thenThrow(new ServiceException("400", "대대댓글은 허용되지 않습니다."));

        assertThatThrownBy(() -> commentService.createComment(testPost.getId(), requestDTO))
                .isInstanceOf(ServiceException.class)
                .hasMessage("대대댓글은 허용되지 않습니다.");
    }
}