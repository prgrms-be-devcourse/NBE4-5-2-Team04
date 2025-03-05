package com.project2.domain.place.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.project2.domain.place.entity.Place;

public interface PlaceRepository extends JpaRepository<Place, Long> {
}
