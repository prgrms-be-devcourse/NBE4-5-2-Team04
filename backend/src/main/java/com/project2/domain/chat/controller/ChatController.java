package com.project2.domain.chat.controller;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project2.domain.chat.dto.ChatMessageRequestDTO;
import com.project2.domain.chat.dto.ChatMessageResponseDTO;
import com.project2.domain.chat.dto.ChatRoomResponseDTO;
import com.project2.domain.chat.entity.ChatMessage;
import com.project2.domain.chat.service.ChatService;
import com.project2.global.dto.RsData;
import com.project2.global.security.SecurityUser;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatController {

	private final ChatService chatService;
	private final SimpMessagingTemplate messagingTemplate;

	// 1:1 대화 내역 조회
	@GetMapping("/room/{opponentId}")
	@Transactional
	public RsData<ChatRoomResponseDTO> getChatRooms(@AuthenticationPrincipal SecurityUser actor,
		@PathVariable Long opponentId) {
		return new RsData<>("200", "성공",
			new ChatRoomResponseDTO(chatService.getOrCreateChatRoom(actor.getId(), opponentId)));
	}

	@PostMapping("/send")
	@Transactional
	public RsData<ChatMessageResponseDTO> sendMessage(@AuthenticationPrincipal SecurityUser actor,
		@RequestBody ChatMessageRequestDTO request) {
		ChatMessage chatMessage = chatService.sendMessage(actor.getId(), request.getChatRoomId(),
			request.getContent());

		ChatMessageResponseDTO responseDTO = new ChatMessageResponseDTO(chatMessage);

		messagingTemplate.convertAndSend("/queue/chatroom/" + request.getChatRoomId(), responseDTO);

		return new RsData<>("200", "성공", responseDTO);
	}
}
