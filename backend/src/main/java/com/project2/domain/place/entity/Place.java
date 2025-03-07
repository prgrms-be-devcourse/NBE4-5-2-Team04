package com.project2.domain.place.entity;

import com.project2.domain.place.enums.Category;
import com.project2.domain.place.enums.Region;
import com.project2.domain.post.entity.Post;
import com.project2.global.entity.BaseTime;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.List;
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Place extends BaseTime {
	@Id
	@Column(name = "place_id")
	private Long id;

	@Column(nullable = false)
	private String name;		// 장소명

	@Column(nullable = false)
	private Double latitude;	// 위도

	@Column(nullable = false)
	private Double longitude;	// 경도

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private Region region;		// 시/도

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private Category category;	// 카테고리

	@OneToMany(mappedBy = "place")
	private List<Post> posts;
}
