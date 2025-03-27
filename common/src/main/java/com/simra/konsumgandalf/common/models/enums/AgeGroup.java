package com.simra.konsumgandalf.common.models.enums;

import com.simra.konsumgandalf.common.models.interfaces.EnumTranslatable;

public enum AgeGroup implements EnumTranslatable {

	NOT_CHOSEN(0), AFTER_2004(1), BETWEEN_2000_AND_2004(2), BETWEEN_1995_AND_1999(3), BETWEEN_1990_AND_1994(4),
	BETWEEN_1985_AND_1989(5), BETWEEN_1980_AND_1984(6), BETWEEN_1975_AND_1979(7), BETWEEN_1970_AND_1974(8),
	BETWEEN_1965_AND_1969(9), BETWEEN_1960_AND_1964(10), BETWEEN_1955_AND_1959(11), BETWEEN_1950_AND_1954(12),
	BEFORE_1950(13);

	private final int value;

	AgeGroup(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}

}
