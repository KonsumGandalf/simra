package com.simra.konsumgandalf.common.models.enums;

public enum SafetyMetricsProfileGroup {

	AGE(0), EXPERIENCE(1), GENDER(2), REGION(3), BEHAVIOR(4), INCIDENTS(5), RIDES(6), DISTANCE(7), DURATION(8),
	CO2_EMISSIONS(9);

	private final int value;

	SafetyMetricsProfileGroup(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}

}
