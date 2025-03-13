package com.project2.domain.chat.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.project2.domain.chat.entity.ChatRoom;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

	@Query("SELECT cr FROM ChatRoom cr WHERE EXISTS (" +
		   "SELECT m FROM cr.members m WHERE m.id IN (:myId, :opponentId))")
	@EntityGraph(attributePaths = {"messages"})
	Optional<ChatRoom> findChatRoomsByMemberIds(@Param("myId") Long myId, @Param("opponentId") Long opponentId);
}
