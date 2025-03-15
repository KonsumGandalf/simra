package com.simra.konsumgandalf.osmPlanet.utils;

public final class ScoreUtils {

	private static final float SCARINESS_FACTOR = 4.4f;

	public static float calculateDangerousScore(int numberOfRides, int numberOfIncidents, int numberOfScaryIncidents) {
		int numberOfNonScaryIncidents = numberOfIncidents - numberOfScaryIncidents;

		return ((SCARINESS_FACTOR * numberOfScaryIncidents + numberOfNonScaryIncidents) / numberOfRides);
	}

}
