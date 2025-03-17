package com.project2.domain.chat.entity;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import com.project2.domain.member.entity.Member;
import com.project2.global.entity.BaseTime;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class ChatRoom extends BaseTime {
	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;

	@ManyToMany
	@JoinTable(
		name = "chat_room_member",
		joinColumns = @JoinColumn(name = "chat_room_id"),
		inverseJoinColumns = @JoinColumn(name = "member_id")
	)
	private Set<Member> members = new HashSet<>();

	@OneToMany(mappedBy = "chatRoom", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private Set<ChatMessage> messages = new HashSet<>();
}
