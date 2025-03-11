package com.project2.domain.place.enums;

import java.util.HashMap;
import java.util.Map;

import lombok.Getter;

@Getter
public enum Region {
	SEOUL("서울특별시"), BUSAN("부산광역시"), DAEGU("대구광역시"), INCHEON("인천광역시"), GWANGJU("광주광역시"), DAEJEON("대전광역시"), ULSAN(
		"울산광역시"), SEJONG("세종특별자치시"), GYEONGGI("경기도"), GANGWON("강원특별자치도"), CHUNGBUK("충청북도"), CHUNGNAM("충청남도"), JEONBUK(
		"전라북도"), JEONNAM("전라남도"), GYEONGBUK("경상북도"), GYEONGNAM("경상남도"), JEJU("제주특별자치도"), ETC("기타");

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
