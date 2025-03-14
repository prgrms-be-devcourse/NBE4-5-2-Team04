package com.project2.domain.chat.repository;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.project2.domain.chat.entity.ChatMessage;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
	@Query(value = "SELECT cm FROM ChatMessage cm WHERE cm.chatRoom.id = :roomId",
		countQuery = "SELECT COUNT(cm) FROM ChatMessage cm WHERE cm.chatRoom.id = :roomId")
	@EntityGraph(attributePaths = {"chatRoom", "sender"})
	Page<ChatMessage> findByChatRoomId(@Param("roomId") UUID roomId, Pageable pageable);
}
