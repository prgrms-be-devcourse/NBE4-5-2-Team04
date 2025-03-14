package com.project2.domain.chat.dto;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.project2.domain.chat.entity.ChatMessage;
import com.project2.domain.chat.entity.ChatRoom;
import com.project2.domain.member.dto.MemberDTO;

import lombok.Getter;

@Getter
public class ChatRoomResponseDTO {
	private final Long id;
	private final Set<MemberDTO> members;
	private final List<ChatMessageResponseDTO> messages;

	public ChatRoomResponseDTO(ChatRoom chatRoom) {
		this.id = chatRoom.getId();
		this.members = chatRoom.getMembers().stream().map(MemberDTO::new).collect(Collectors.toSet());
		this.messages = chatRoom.getMessages().stream()
			.sorted(Comparator.comparing(ChatMessage::getCreatedDate))
			.map(ChatMessageResponseDTO::new)
			.collect(Collectors.toList());
	}
}
