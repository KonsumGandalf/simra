package com.simra.konsumgandalf.common.models.enums;

import com.simra.konsumgandalf.common.models.interfaces.EnumTranslatable;

public enum SimraRegionGroup implements EnumTranslatable {

	NOT_CHOSEN(0), BERLIN_POTSDAM(1), LONDON(2), OTHER(3), BERN(4), PFORZHEIM_ENZKREIS(5), AUGSBURG(6), RUHR_REGION(7),
	STUTTGART(8), LEIPZIG(9), WUPPERTAL_SOLINGEN_REMSCHEID(10), DUESSELDORF(11), EICHWALDE_ZEUTHEN_SCHULZENDORF(12),
	HANNOVER(13), BIELEFELD(14), MUNICH(15), ZES_EXPERIMENTAL(16), KONSTANZ(17), WEIMAR(18), KARLSRUHE(20), ALL(19);

	private final int value;

	SimraRegionGroup(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}

}
