package com.project2.domain.chat.controller;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.project2.domain.chat.dto.ChatMessageRequestDTO;
import com.project2.domain.chat.dto.ChatMessageResponseDTO;
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

	@GetMapping("/room/find/{opponentId}")
	public RsData<UUID> findOrCreateChatRoomId(@AuthenticationPrincipal SecurityUser actor,
		@PathVariable Long opponentId) {
		return new RsData<>("200", "성공", chatService.findOrCreateChatRoomId(actor.getId(), opponentId));
	}

	@GetMapping("/room/{roomId}")
	public RsData<Page<ChatMessageResponseDTO>> getChatMessages(
		@PathVariable UUID roomId,
		@RequestParam(defaultValue = "0") int offset,
		@RequestParam(defaultValue = "10") int size) {

		Pageable pageable = PageRequest.of(offset / size, size, Sort.by("createdDate").descending());
		return new RsData<>("200", "성공", chatService.getChatMessages(roomId, pageable));
	}

	@PostMapping("/send")
	public RsData<ChatMessageResponseDTO> sendMessage(@AuthenticationPrincipal SecurityUser actor,
		@RequestBody ChatMessageRequestDTO request) {
		ChatMessageResponseDTO responseDTO = chatService.sendMessage(actor.getId(), request.getChatRoomId(),
			request.getContent());

		messagingTemplate.convertAndSend("/queue/chatroom/" + request.getChatRoomId(), responseDTO);

		return new RsData<>("200", "성공", responseDTO);
	}
}
