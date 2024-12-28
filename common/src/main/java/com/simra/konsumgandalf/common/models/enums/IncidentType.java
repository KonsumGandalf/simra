package com.simra.konsumgandalf.common.models.enums;

import com.simra.konsumgandalf.common.models.interfaces.EnumTranslatable;

public enum IncidentType implements EnumTranslatable {

	DUMMY_INCIDENT(-5), NOTHING(0), CLOSE_PASS(1), PULLING_IN_OUT(2), NEAR_LEFT_RIGHT_HOOK(3), APPROACHING_HEAD_ON(4),
	TAILGATING(5), NEAR_DOORING(6), DODGING_OBSTACLE(7), OTHER(8);

	private final int value;

	IncidentType(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}

}
