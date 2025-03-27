package com.simra.konsumgandalf.common.models.enums;

import com.simra.konsumgandalf.common.models.interfaces.EnumTranslatable;

public enum Gender implements EnumTranslatable {

	NOT_CHOSEN(0), MALE(1), FEMALE(2), OTHER(3);

	private final int value;

	Gender(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}

}
