package com.project2.domain.chat.service;

import java.util.Optional;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project2.domain.chat.dto.ChatMessageResponseDTO;
import com.project2.domain.chat.dto.ChatRoomResponseDTO;
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

	// 유저 간 메시지 조회
	@Transactional(readOnly = true)
	public ChatRoomResponseDTO getOrCreateChatRoom(Long myId, Long opponentId) {
		Optional<ChatRoom> existingRoom = chatRoomRepository.findChatRoomByMemberIds(myId, opponentId);

		if (existingRoom.isPresent()) {
			return new ChatRoomResponseDTO(existingRoom.get());
		}

		// 채팅방이 없으면 새로 생성
		Member me = memberRepository.findById(myId)
			.orElseThrow(() -> new RuntimeException("내 정보를 찾을 수 없습니다."));
		Member opponent = memberRepository.findById(opponentId)
			.orElseThrow(() -> new RuntimeException("상대 정보를 찾을 수 없습니다."));

		ChatRoom newChatRoom = new ChatRoom();
		newChatRoom.setMembers(Set.of(me, opponent));

		newChatRoom = chatRoomRepository.save(newChatRoom);
		return new ChatRoomResponseDTO(newChatRoom);
	}

	// 메시지 전송
	@Transactional
	public ChatMessageResponseDTO sendMessage(Long actorId, Long chatRoomId, String content) {
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
