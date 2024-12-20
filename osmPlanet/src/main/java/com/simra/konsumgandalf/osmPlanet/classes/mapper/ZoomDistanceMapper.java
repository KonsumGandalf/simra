package com.simra.konsumgandalf.osmPlanet.classes.mapper;

import java.util.HashMap;

/**
 * This class maps zoom levels to distances that should be displayed at that zoom level.
 */
public class ZoomDistanceMapper {

	private final HashMap<Integer, Integer> map = new HashMap<>();

	public ZoomDistanceMapper() {
		map.put(0, 40_000_000);
		map.put(1, 20_000_000);
		map.put(2, 10_000_000);
		map.put(3, 5_000_000);
		map.put(4, 2_500_000);
		map.put(5, 1_250_000);
		map.put(6, 625_000);
		map.put(7, 312_500);
		map.put(8, 156_250);
		map.put(9, 78_125);
		map.put(10, 39_063);
		map.put(11, 19_531);
		map.put(12, 19_531);
		map.put(13, 14_765);
		map.put(14, 9_765);
		map.put(15, 4_882);
		map.put(16, 2_441);
		map.put(17, 1_220);
		map.put(18, 610);
		map.put(19, 305);
		map.put(20, 152);
	}

	public int getDistanceForZoom(int zoomLevel) {
		return map.getOrDefault(zoomLevel, 0);
	}

}
