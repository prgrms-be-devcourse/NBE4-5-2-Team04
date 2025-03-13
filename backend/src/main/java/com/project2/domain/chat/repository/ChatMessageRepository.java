package com.project2.domain.chat.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.project2.domain.chat.entity.ChatMessage;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
}
