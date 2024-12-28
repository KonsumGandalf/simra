package com.simra.konsumgandalf.common.models.enums;

import com.simra.konsumgandalf.common.models.interfaces.EnumTranslatable;

public enum ParticipantType implements EnumTranslatable {
	BUS_COACH(1),
	CYCLIST(2),
	PEDESTRIAN(3),
	DELIVERY_VAN(4),
	LORRY_TRUCK(5),
	MOTORCYCLIST(6),
	CAR(7),
	TAXI_CAB(8),
	OTHER(9),
	ELECTRIC_SCOOTER(10);

	private final int value;

	ParticipantType(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}
}
