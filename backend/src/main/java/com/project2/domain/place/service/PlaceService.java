package com.project2.domain.place.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project2.domain.place.entity.Place;
import com.project2.domain.place.enums.Category;
import com.project2.domain.place.enums.Region;
import com.project2.domain.place.repository.PlaceRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PlaceService {
	private final PlaceRepository placeRepository;

	@Transactional
	public Place savePlace(Long placeId, String name, Double latitude, Double longitude, String region,
		String category) {
		Region regionEnum = Region.fromKrRegion(region);
		Category categoryEnum = Category.fromKrCategory(category);

		return placeRepository.findById(placeId).orElseGet(() -> {
			Place newPlace = Place.builder()
				.id(placeId)
				.name(name)
				.latitude(latitude)
				.longitude(longitude)
				.region(regionEnum)
				.category(categoryEnum)
				.build();
			return placeRepository.save(newPlace);
		});
	}
}
