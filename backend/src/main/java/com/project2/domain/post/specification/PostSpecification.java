package com.project2.domain.post.specification;

import org.springframework.data.jpa.domain.Specification;

import com.project2.domain.place.entity.Place;
import com.project2.domain.post.entity.Post;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;

public class PostSpecification {
	public static Specification<Post> filterByPlaceAndCategory(String placeName, String placeCategory) {
		return (root, query, criteriaBuilder) -> {
			Predicate predicate = criteriaBuilder.conjunction();

			if (placeName != null && !placeName.isEmpty()) {
				Join<Post, Place> placeJoin = root.join("place");
				predicate = criteriaBuilder.and(predicate, criteriaBuilder.like(placeJoin.get("name"), "%" + placeName + "%"));
			}

			if (placeCategory != null && !placeCategory.isEmpty()) {
				Join<Post, Place> placeJoin = root.join("place");
				predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(placeJoin.get("category"), placeCategory));
			}

			return predicate;
		};
	}
}
