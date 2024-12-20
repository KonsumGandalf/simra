package com.simra.konsumgandalf.osmPlanet.classes.mapper;

import com.simra.konsumgandalf.osmPlanet.classes.enums.RoadTypes;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

@ExtendWith(MockitoExtension.class)
public class ZoomRoadTypeMapperTest {

	private ZoomRoadTypeMapper roadTypeMapper = new ZoomRoadTypeMapper();

	@Test
	public void testZoomLevel10() {
		List<RoadTypes> result = roadTypeMapper.getRoadTypes(10);
		Assertions.assertIterableEquals(List.of(), result);
	}

	@Test
	public void testZoomLevel11() {
		List<RoadTypes> result = roadTypeMapper.getRoadTypes(11);
		Assertions.assertIterableEquals(List.of(RoadTypes.PRIMARY, RoadTypes.PRIMARY_LINK), result);
	}

	@Test
	public void testZoomLevel13() {
		List<RoadTypes> result = roadTypeMapper.getRoadTypes(13);
		Assertions.assertIterableEquals(
				List.of(RoadTypes.PRIMARY, RoadTypes.PRIMARY_LINK, RoadTypes.SECONDARY, RoadTypes.SECONDARY_LINK),
				result);
	}

	@Test
	public void testZoomLevel14() {
		List<RoadTypes> result = roadTypeMapper.getRoadTypes(14);
		Assertions.assertIterableEquals(List.of(RoadTypes.PRIMARY, RoadTypes.PRIMARY_LINK, RoadTypes.SECONDARY,
				RoadTypes.SECONDARY_LINK, RoadTypes.TERTIARY, RoadTypes.TERTIARY_LINK), result);
	}

	@Test
	public void testZoomLevel15() {
		List<RoadTypes> result = roadTypeMapper.getRoadTypes(15);
		Assertions.assertIterableEquals(List.of(RoadTypes.PRIMARY, RoadTypes.PRIMARY_LINK, RoadTypes.SECONDARY,
				RoadTypes.SECONDARY_LINK, RoadTypes.TERTIARY, RoadTypes.TERTIARY_LINK, RoadTypes.UNCLASSIFIED,
				RoadTypes.RESIDENTIAL, RoadTypes.LIVING_STREET, RoadTypes.BRIDLEWAY), result);
	}

	@Test
	public void testZoomLevel18() {
		List<RoadTypes> result = roadTypeMapper.getRoadTypes(18);
		Assertions.assertIterableEquals(List.of(RoadTypes.values()), result);
	}

}
