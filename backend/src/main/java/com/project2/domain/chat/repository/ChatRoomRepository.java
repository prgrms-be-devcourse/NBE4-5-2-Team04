package com.project2.domain.chat.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.project2.domain.chat.entity.ChatRoom;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
	@Query("SELECT cr FROM ChatRoom cr " +
		   "WHERE EXISTS (SELECT 1 FROM cr.members m1 WHERE m1.id = :myId) " +
		   "AND EXISTS (SELECT 1 FROM cr.members m2 WHERE m2.id = :opponentId) " +
		   "AND SIZE(cr.members) = 2")
	@EntityGraph(attributePaths = {"messages", "members"})
	Optional<ChatRoom> findChatRoomByMemberIds(@Param("myId") Long myId, @Param("opponentId") Long opponentId);

}
