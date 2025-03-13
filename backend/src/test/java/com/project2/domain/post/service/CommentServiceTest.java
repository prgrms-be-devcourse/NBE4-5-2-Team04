package com.project2.domain.post.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.annotation.Transactional;

import com.project2.domain.member.entity.Member;
import com.project2.domain.member.repository.MemberRepository;
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

@ExtendWith(MockitoExtension.class)
@Transactional
class CommentServiceTest {

	@Mock
	private MemberRepository memberRepository;

	@Mock
	private CommentRepository commentRepository;

	@Mock
	private PostRepository postRepository;

	@Mock
	private Rq rq;
	@Mock
	private CommentMapper commentMapper;

	@InjectMocks
	private CommentService commentService;

	private Member testUser;
	private Post testPost;
	private Comment parentComment;
	private Comment childComment;
	private CommentRequestDTO requestDTO;

	@BeforeEach
	void setUp() {
		testUser = Member.builder().id(1L).email("test@example.com").nickname("TestUser").build();
		testPost = Post.builder().id(1L).build();

		parentComment = Comment.builder()
			.id(101L)
			.content("부모 댓글")
			.post(testPost)
			.member(testUser)
			.depth(0)
			.build();

		childComment = Comment.builder()
			.id(102L)
			.content("대댓글")
			.post(testPost)
			.member(testUser)
			.depth(1)
			.parent(parentComment)
			.build();

		requestDTO = new CommentRequestDTO("Updated Comment", null);
	}

	@Test
	@DisplayName("댓글 작성 성공 - 부모 댓글")
	void createParentComment_Success() {
		when(rq.getActor()).thenReturn(testUser);
		when(memberRepository.findById(testUser.getId())).thenReturn(Optional.of(testUser));
		when(postRepository.findById(testPost.getId())).thenReturn(Optional.of(testPost));
		when(commentMapper.toEntity(any(), any(), any(), eq(null))).thenReturn(parentComment);
		when(commentRepository.save(any(Comment.class))).thenReturn(parentComment);
		when(commentMapper.toResponseDTO(any(), anyString())).thenReturn(
			new CommentResponseDTO(101L, "부모 댓글", "TestUser", null)
		);

		RsData<CommentResponseDTO> response = commentService.createComment(testPost.getId(), requestDTO);

		assertThat(response.getCode()).isEqualTo("200");
		assertThat(response.getData()).isNotNull();
		assertThat(response.getData().getContent()).isEqualTo("부모 댓글");
	}

	@Test
	@DisplayName("댓글 작성 성공 - 대댓글")
	void createChildComment_Success() {
		when(rq.getActor()).thenReturn(testUser);
		when(memberRepository.findById(testUser.getId())).thenReturn(Optional.of(testUser));
		when(postRepository.findById(testPost.getId())).thenReturn(Optional.of(testPost));
		when(commentRepository.findById(parentComment.getId())).thenReturn(Optional.of(parentComment));
		when(commentMapper.toEntity(any(), any(), any(), any())).thenReturn(childComment);
		when(commentRepository.save(any(Comment.class))).thenReturn(childComment);
		when(commentMapper.toResponseDTO(any(), anyString())).thenReturn(
			new CommentResponseDTO(102L, "대댓글", "TestUser", 101L)
		);

		CommentRequestDTO requestWithParent = new CommentRequestDTO("대댓글", parentComment.getId());
		RsData<CommentResponseDTO> response = commentService.createComment(testPost.getId(), requestWithParent);

		assertThat(response.getCode()).isEqualTo("200");
		assertThat(response.getData().getParentId()).isEqualTo(parentComment.getId());
	}

	@Test
	@DisplayName("댓글 수정 성공")
	void updateComment_Success() {
		when(rq.getActor()).thenReturn(testUser);
		when(memberRepository.findById(testUser.getId())).thenReturn(Optional.of(testUser));
		when(commentRepository.findById(parentComment.getId())).thenReturn(Optional.of(parentComment));
		when(commentMapper.toResponseDTO(any(), anyString())).thenReturn(
			new CommentResponseDTO(101L, "Updated Comment", "TestUser", null)
		);

		RsData<CommentResponseDTO> response = commentService.updateComment(parentComment.getId(), requestDTO);

		assertThat(response.getCode()).isEqualTo("200");
		assertThat(parentComment.getContent()).isEqualTo(requestDTO.getContent());
	}

	@Test
	@DisplayName("댓글 수정 실패 - 권한 없음")
	void updateComment_Fail_NoPermission() {
		Member anotherUser = Member.builder().id(2L).build();
		when(rq.getActor()).thenReturn(anotherUser);
		when(commentRepository.findById(parentComment.getId())).thenReturn(Optional.of(parentComment));

		assertThatThrownBy(() -> commentService.updateComment(parentComment.getId(), requestDTO))
			.isInstanceOf(ServiceException.class)
			.hasMessage("댓글 수정 권한이 없습니다.");
	}

	@Test
	@DisplayName("댓글 삭제 성공")
	void deleteComment_Success() {
		when(rq.getActor()).thenReturn(testUser);
		when(commentRepository.findById(parentComment.getId())).thenReturn(Optional.of(parentComment));

		RsData<Empty> response = commentService.deleteComment(parentComment.getId());

		assertThat(response.getCode()).isEqualTo("200");
		verify(commentRepository, times(1)).delete(parentComment);
	}

	@Test
	@DisplayName("댓글 삭제 실패 - 권한 없음")
	void deleteComment_Fail_NoPermission() {
		Member anotherUser = Member.builder().id(2L).build();
		when(rq.getActor()).thenReturn(anotherUser);
		when(commentRepository.findById(parentComment.getId())).thenReturn(Optional.of(parentComment));

		assertThatThrownBy(() -> commentService.deleteComment(parentComment.getId()))
			.isInstanceOf(ServiceException.class)
			.hasMessage("댓글 삭제 권한이 없습니다.");

		verify(commentRepository, never()).delete(parentComment);
	}
}