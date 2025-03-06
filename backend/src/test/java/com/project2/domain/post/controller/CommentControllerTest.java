package com.project2.domain.post.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project2.domain.post.dto.comment.CommentRequestDTO;
import com.project2.domain.post.dto.comment.CommentResponseDTO;
import com.project2.domain.post.service.CommentService;
import com.project2.global.dto.RsData;
import com.project2.global.util.Ut;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import jakarta.servlet.http.Cookie;
import java.util.Map;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class CommentControllerTest {

    @Mock
    private CommentService commentService;

    @InjectMocks
    private CommentController commentController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private Cookie accessTokenCookie;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(commentController).build();
        objectMapper = new ObjectMapper();
        String SECRET_KEY = "abcdefghijklmnopqrstuvwxyz123456";
        int TOKEN_EXPIRATION = 3600;
        String accessToken = Ut.Jwt.createToken(SECRET_KEY, TOKEN_EXPIRATION, Map.of(
                "id", 1L,
                "email", "test@example.com"
        ));

        accessTokenCookie = new Cookie("accessToken", accessToken);
        accessTokenCookie.setHttpOnly(true);
        accessTokenCookie.setSecure(true);
        accessTokenCookie.setPath("/");
    }

    @Test
    @DisplayName("댓글 작성 성공 - JWT 쿠키 포함 요청")
    void createComment_Success() throws Exception {
        // Given
        CommentRequestDTO requestDTO = new CommentRequestDTO("Test Comment", null);
        CommentResponseDTO responseDTO = new CommentResponseDTO(1L, "Test Comment", "TestUser");

        when(commentService.createComment(any(), any()))
                .thenReturn(new RsData<>("200", "댓글이 성공적으로 작성되었습니다.", responseDTO));

        // When
        RequestBuilder request = MockMvcRequestBuilders
                .post("/api/posts/1/comments")
                .cookie(accessTokenCookie)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO));

        // Then
        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.data.content").value("Test Comment"));
    }

    @Test
    @DisplayName("댓글 조회 성공")
    void getComments_Success() throws Exception {
        // Given
        List<CommentResponseDTO> comments = List.of(new CommentResponseDTO(1L, "Test Comment", "TestUser"));

        when(commentService.getComments(any()))
                .thenReturn(new RsData<>("200", "댓글 목록 조회 성공", comments));

        // When
        RequestBuilder request = MockMvcRequestBuilders
                .get("/api/posts/1/comments")
                .cookie(accessTokenCookie)
                .contentType(MediaType.APPLICATION_JSON);

        // Then
        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.data[0].content").value("Test Comment"));
    }

    @Test
    @DisplayName("댓글 수정 성공")
    void updateComment_Success() throws Exception {
        // Given
        CommentRequestDTO requestDTO = new CommentRequestDTO("Updated Comment", null);
        CommentResponseDTO responseDTO = new CommentResponseDTO(1L, "Updated Comment", "TestUser");

        when(commentService.updateComment(any(), any()))
                .thenReturn(new RsData<>("200", "댓글이 성공적으로 수정되었습니다.", responseDTO));

        // When
        RequestBuilder request = MockMvcRequestBuilders
                .put("/api/comments/1")
                .cookie(accessTokenCookie)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO));

        // Then
        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.data.content").value("Updated Comment"));
    }

    @Test
    @DisplayName("댓글 삭제 성공")
    void deleteComment_Success() throws Exception {
        // Given
        when(commentService.deleteComment(any()))
                .thenReturn(new RsData<>("200", "댓글이 성공적으로 삭제되었습니다."));

        // When
        RequestBuilder request = MockMvcRequestBuilders
                .delete("/api/comments/1")
                .cookie(accessTokenCookie)
                .contentType(MediaType.APPLICATION_JSON);

        // Then
        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.msg").value("댓글이 성공적으로 삭제되었습니다."));
    }
}
