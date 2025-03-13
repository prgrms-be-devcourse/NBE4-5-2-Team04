package com.project2.domain.chat.dto;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import com.project2.domain.chat.entity.ChatMessage;
import com.project2.domain.chat.entity.ChatRoom;

import lombok.Getter;

@Getter
public class ChatRoomResponseDTO {
	private final Long id;
	private final List<ChatMessageResponseDTO> messages;

	public ChatRoomResponseDTO(ChatRoom chatRoom) {
		this.id = chatRoom.getId();
		this.messages = chatRoom.getMessages().stream()
			.sorted(Comparator.comparing(ChatMessage::getCreatedDate))
			.map(ChatMessageResponseDTO::new)
			.collect(Collectors.toList());
	}
}
