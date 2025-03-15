package com.project2.domain.chat.service;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project2.domain.chat.dto.ChatMessageResponseDTO;
import com.project2.domain.chat.entity.ChatMessage;
import com.project2.domain.chat.entity.ChatRoom;
import com.project2.domain.chat.repository.ChatMessageRepository;
import com.project2.domain.chat.repository.ChatRoomRepository;
import com.project2.domain.member.entity.Member;
import com.project2.domain.member.repository.MemberRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChatService {

	private final ChatMessageRepository chatMessageRepository;
	private final ChatRoomRepository chatRoomRepository;
	private final MemberRepository memberRepository;

	@Transactional
	public UUID findOrCreateChatRoomId(Long myId, Long opponentId) {
		Optional<UUID> existingRoomId = chatRoomRepository.findChatRoomIdByMemberIds(myId, opponentId);
		if (existingRoomId.isPresent()) {
			return existingRoomId.get();
		}

		Member me = memberRepository.findById(myId)
			.orElseThrow(() -> new RuntimeException("내 정보를 찾을 수 없습니다."));
		Member opponent = memberRepository.findById(opponentId)
			.orElseThrow(() -> new RuntimeException("상대 정보를 찾을 수 없습니다."));

		ChatRoom newChatRoom = new ChatRoom();
		newChatRoom.setMembers(Set.of(me, opponent));
		return chatRoomRepository.save(newChatRoom).getId();
	}

	@Transactional(readOnly = true)
	public Page<ChatMessageResponseDTO> getChatMessages(UUID roomId, Pageable pageable) {
		Page<ChatMessage> messages = chatMessageRepository.findByChatRoomId(roomId, pageable);
		return messages.map(ChatMessageResponseDTO::new);
	}

	// 메시지 전송
	@Transactional
	public ChatMessageResponseDTO sendMessage(Long actorId, UUID chatRoomId, String content) {
		Member actor = memberRepository.getReferenceById(actorId);
		ChatRoom chatRoom = chatRoomRepository.getReferenceById(chatRoomId);

		ChatMessage chatMessage = ChatMessage.builder()
			.sender(actor)
			.chatRoom(chatRoom)
			.content(content)
			.build();

		chatMessage = chatMessageRepository.save(chatMessage);
		return new ChatMessageResponseDTO(chatMessage);
	}
}
