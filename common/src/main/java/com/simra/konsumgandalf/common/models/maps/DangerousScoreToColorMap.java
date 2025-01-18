package com.simra.konsumgandalf.common.models.maps;

import com.google.common.collect.ImmutableBiMap;
import com.simra.konsumgandalf.common.constants.DangerousColors;

import java.util.Map;

public class DangerousScoreToColorMap {

	public static final Map<Float, String> DANGEROUS_SCORE_TO_COLOR_MAP = ImmutableBiMap.of(0.5f,
			DangerousColors.RED_500, 0.25f, DangerousColors.ORANGE_500, 0.1f, DangerousColors.AMBER_500, 0.04f,
			DangerousColors.LIME_500, 0f, DangerousColors.GREEN_500, -1f, DangerousColors.NEUTRAL_200);

	public static String getColorForScore(float score) {
		return DANGEROUS_SCORE_TO_COLOR_MAP.entrySet()
			.stream()
			.filter(e -> score >= e.getKey())
			.findFirst()
			.orElse(Map.entry(-1f, DangerousColors.NEUTRAL_200))
			.getValue();
	}

}
