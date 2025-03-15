package com.project2.domain.chat.dto;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageRequestDTO {
	private UUID chatRoomId;
	private String content;
}
