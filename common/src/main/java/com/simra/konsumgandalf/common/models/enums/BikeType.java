package com.simra.konsumgandalf.common.models.enums;

import com.simra.konsumgandalf.common.models.interfaces.EnumTranslatable;

public enum BikeType implements EnumTranslatable {
	NOT_CHOSEN(0),
	CITY_TREKKING_BIKE(1),
	ROAD_RACING_BIKE(2),
	E_BIKE(3),
	RECUMBENT_BICYCLE(4),
	FREIGHT_BICYCLE(5),
	TANDEM_BICYCLE(6),
	MOUNTAIN_BIKE(7),
	OTHER(8);

	private final int value;

	BikeType(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}

	public static BikeType fromValue(int value) {
		for (BikeType bikeType : values()) {
			if (bikeType.value == value) {
				return bikeType;
			}
		}
		throw new IllegalArgumentException("Invalid bike type value: " + value);
	}
}
