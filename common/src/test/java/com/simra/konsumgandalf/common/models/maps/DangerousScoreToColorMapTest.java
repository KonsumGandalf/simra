package com.simra.konsumgandalf.common.models.maps;

import com.simra.konsumgandalf.common.constants.DangerousColors;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class DangerousScoreToColorMapTest {

	@Test
	public void testGetColorForScore_RED() {
		String color = DangerousScoreToColorMap.getColorForScore(0.5f);
		assertEquals(DangerousColors.RED_500, color);
		assertEquals("#EF4444", color);

		color = DangerousScoreToColorMap.getColorForScore(10f);
		assertEquals(DangerousColors.RED_500, color);
		assertEquals("#EF4444", color);
	}

	@Test
	public void testGetColorForScore_ORANGE() {
		String color = DangerousScoreToColorMap.getColorForScore(0.25f);
		assertEquals(DangerousColors.ORANGE_500, color);
		assertEquals("#F97316", color);

		color = DangerousScoreToColorMap.getColorForScore(0.499f);
		assertEquals(DangerousColors.ORANGE_500, color);
		assertEquals("#F97316", color);
	}

	@Test
	public void testGetColorForScore_AMBER() {
		String color = DangerousScoreToColorMap.getColorForScore(0.1f);
		assertEquals(DangerousColors.AMBER_500, color);
		assertEquals("#F59E0B", color);

		color = DangerousScoreToColorMap.getColorForScore(0.249f);
		assertEquals(DangerousColors.AMBER_500, color);
		assertEquals("#F59E0B", color);
	}

	@Test
	public void testGetColorForScore_LIME() {
		String color = DangerousScoreToColorMap.getColorForScore(0.04f);
		assertEquals(DangerousColors.LIME_500, color);
		assertEquals("#84CC16", color);

		color = DangerousScoreToColorMap.getColorForScore(0.09f);
		assertEquals(DangerousColors.LIME_500, color);
		assertEquals("#84CC16", color);
	}

	@Test
	public void testGetColorForScore_GREEN() {
		String color = DangerousScoreToColorMap.getColorForScore(0.0f);
		assertEquals(DangerousColors.GREEN_500, color);
		assertEquals("#22C55E", color);

		color = DangerousScoreToColorMap.getColorForScore(0.039f);
		assertEquals(DangerousColors.GREEN_500, color);
		assertEquals("#22C55E", color);
	}

	@Test
	public void testGetColorForScore_NEUTRAL() {
		String color = DangerousScoreToColorMap.getColorForScore(-1f);
		assertEquals(DangerousColors.NEUTRAL_200, color);
		assertEquals("#E5E5E5", color);

		color = DangerousScoreToColorMap.getColorForScore(-10f);
		assertEquals(DangerousColors.NEUTRAL_200, color);
		assertEquals("#E5E5E5", color);
	}

}
