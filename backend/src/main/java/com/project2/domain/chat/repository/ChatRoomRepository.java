package com.project2.domain.chat.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.project2.domain.chat.entity.ChatRoom;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, UUID> {
	@Query("SELECT cr.id FROM ChatRoom cr " +
		   "WHERE EXISTS (SELECT 1 FROM cr.members m1 WHERE m1.id = :myId) " +
		   "AND EXISTS (SELECT 1 FROM cr.members m2 WHERE m2.id = :opponentId) " +
		   "AND SIZE(cr.members) = 2")
	Optional<UUID> findChatRoomIdByMemberIds(@Param("myId") Long myId, @Param("opponentId") Long opponentId);

}
