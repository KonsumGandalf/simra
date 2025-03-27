package com.simra.konsumgandalf.common.utils;

import static java.lang.Math.toIntExact;

public final class ScoreUtils {

	private static final float SCARINESS_FACTOR = 4.4f;

	public static float calculateDangerousScore(int numberOfRides, int numberOfIncidents, int numberOfScaryIncidents) {
		int numberOfNonScaryIncidents = numberOfIncidents - numberOfScaryIncidents;

		return ((SCARINESS_FACTOR * numberOfScaryIncidents + numberOfNonScaryIncidents) / numberOfRides);
	}

	public static float calculateDangerousScore(Long numberOfRides, Long numberOfIncidents,
			Long numberOfScaryIncidents) {
		return calculateDangerousScore(toIntExact(numberOfRides), toIntExact(numberOfIncidents),
				toIntExact(numberOfScaryIncidents));
	}

}
