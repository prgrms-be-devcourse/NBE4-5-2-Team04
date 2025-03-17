package com.project2.domain.chat.dto;

import java.util.UUID;

import com.project2.domain.chat.entity.ChatRoom;
import com.project2.domain.member.entity.Member;

import lombok.Getter;

@Getter
public class ChatRoomResponseDTO {
	private final UUID id;
	private final Member opponent;

	public ChatRoomResponseDTO(ChatRoom chatRoom, Long actorId) {
		this.id = chatRoom.getId();
		this.opponent = chatRoom.getMembers()
			.stream()
			.filter(member -> !member.getId().equals(actorId))
			.findFirst()
			.orElseThrow(() -> new IllegalStateException("ChatRoom must have exactly one opponent"));
	}

}
