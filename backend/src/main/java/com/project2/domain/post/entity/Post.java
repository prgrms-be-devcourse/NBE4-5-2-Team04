package com.project2.domain.post.entity;

import com.project2.domain.member.entity.Member;
import com.project2.domain.place.entity.Place;
import com.project2.global.entity.BaseTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
public class Post extends BaseTime {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "post_id")
	private Long id;

	@Column(nullable = false, columnDefinition = "TEXT")
	private String content;
	private String title;
	private Double latitude;
	private Double longitude;
	@ManyToOne
	@JoinColumn(nullable = false)
	private Member member;

	@ManyToOne
	private Place place;
}
