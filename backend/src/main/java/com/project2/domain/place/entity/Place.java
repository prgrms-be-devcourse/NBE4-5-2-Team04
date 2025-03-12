package com.project2.domain.place.entity;

import java.util.ArrayList;
import java.util.List;

import com.project2.domain.place.enums.Category;
import com.project2.domain.place.enums.Region;
import com.project2.domain.post.entity.Post;
import com.project2.global.entity.BaseTime;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Place extends BaseTime {
	@Id
	private Long id;

	@Column(nullable = false)
	private String name;

	@Column(nullable = false)
	private Double latitude;

	@Column(nullable = false)
	private Double longitude;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private Region region;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private Category category;

	@OneToMany(mappedBy = "place", cascade = CascadeType.ALL, orphanRemoval = true)
	@Builder.Default
	private List<Post> posts = new ArrayList<>();

	public String getKrCategory() {
		return category.getKrCategory();
	}
}