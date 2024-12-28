package com.simra.konsumgandalf.common.models.enums;

import com.simra.konsumgandalf.common.models.interfaces.EnumTranslatable;

public enum PhoneLocation implements EnumTranslatable {

	POCKET(0), HANDLEBAR(1), JACKET_POCKET(2), HAND(3), BASKET_PANNIER(4), BACKPACK_BAG(5), OTHER(6);

	private final int value;

	PhoneLocation(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}

}
