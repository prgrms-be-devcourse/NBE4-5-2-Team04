package com.project2.domain.chat.service;

import java.util.Optional;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
	public ChatRoom getOrCreateChatRoom(Long myId, Long opponentId) {
		Optional<ChatRoom> existingRoom = chatRoomRepository.findChatRoomsByMemberIds(myId, opponentId);

		if (existingRoom.isPresent()) {
			return existingRoom.get();
		}

		// 채팅방이 없으면 새로 생성
		Member me = memberRepository.findById(myId)
			.orElseThrow(() -> new RuntimeException("내 정보를 찾을 수 없습니다."));
		Member opponent = memberRepository.findById(opponentId)
			.orElseThrow(() -> new RuntimeException("상대 정보를 찾을 수 없습니다."));

		ChatRoom newChatRoom = new ChatRoom();
		newChatRoom.setMembers(Set.of(me, opponent));

		return chatRoomRepository.save(newChatRoom);
	}

	// 메시지 전송
	public ChatMessage sendMessage(Long actorId, Long opponentId, Long chatRoomId, String content) {
		Member actor = memberRepository.getReferenceById(actorId);
		Member opponent = memberRepository.getReferenceById(opponentId);
		ChatRoom chatRoom = chatRoomRepository.getReferenceById(chatRoomId);

		ChatMessage chatMessage = ChatMessage.builder()
			.sender(actor)
			.receiver(opponent)
			.chatRoom(chatRoom)
			.content(content)
			.isRead(false)
			.build();

		return chatMessageRepository.save(chatMessage);
	}
}
