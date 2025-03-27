package com.simra.konsumgandalf.common.models.enums;

import com.simra.konsumgandalf.common.models.interfaces.EnumTranslatable;

public enum ExperienceGroup implements EnumTranslatable {

	NOT_CHOSEN(0), BEGINNER(4), INTERMEDIATE(3), ADVANCED(2), EXPERT(1);

	private final int value;

	ExperienceGroup(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}

	public static ExperienceGroup fromValue(int value) {
		for (ExperienceGroup experienceGroup : values()) {
			if (experienceGroup.value == value) {
				return experienceGroup;
			}
		}
		throw new IllegalArgumentException("Invalid experience group value: " + value);
	}

}
