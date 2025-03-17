package com.project2.domain.place.enums;

import java.util.HashMap;
import java.util.Map;

import lombok.Getter;

@Getter
public enum Region {
	SEOUL("서울"), BUSAN("부산"), DAEGU("대구"), INCHEON("인천"), GWANGJU("광주"), DAEJEON("대전"), ULSAN(
		"울산"), SEJONG("세종특별자치시"), GYEONGGI("경기"), GANGWON("강원특별자치도"), CHUNGBUK("충북"), CHUNGNAM("충남"), JEONBUK(
		"전북특별자치도"), JEONNAM("전남"), GYEONGBUK("경북"), GYEONGNAM("경남"), JEJU("제주특별자치도"), ETC("기타");

	private final String krRegion;

	// 지역 한글명을 받아 코드로 변환 해주주는 부분.
	Region(String krRegion) {
		this.krRegion = krRegion;
	}

	private static final Map<String, Region> REGION_MAP = new HashMap<>();

	static {
		for (Region region : Region.values()) {
			REGION_MAP.put(region.krRegion, region);
		}
	}

	public static Region fromKrRegion(String krRegion) {
		return REGION_MAP.getOrDefault(krRegion, ETC);
	}
}