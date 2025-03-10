package com.project2.domain.post.entity;

import com.project2.domain.member.entity.Member;
import com.project2.global.entity.BaseTime;

import com.project2.global.exception.ServiceException;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
public class Comment extends BaseTime {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "comment_id")
	private Long id;

	@Column(nullable = false, columnDefinition = "TEXT")
	private String content;

	private int depth;

	@ManyToOne
	@JoinColumn(nullable = false)
	private Post post;

	@ManyToOne
	@JoinColumn(nullable = false)
	private Member member;

	@ManyToOne
	@JoinColumn(name = "parent_comment_id")
	private Comment parent;

	@OneToMany(mappedBy = "parent", cascade = CascadeType.REMOVE, orphanRemoval = true)
	@Builder.Default
	private List<Comment> children = new ArrayList<>();

	public void updateContent(String newContent) {
		if (newContent == null || newContent.trim().isEmpty()) {
			throw new ServiceException("400", "댓글 내용은 비어 있을 수 없습니다.");
		}
		this.content = newContent;
	}
}
