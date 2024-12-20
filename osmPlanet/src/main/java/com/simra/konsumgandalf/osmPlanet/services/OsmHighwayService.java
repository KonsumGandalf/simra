package com.simra.konsumgandalf.osmPlanet.services;

import com.simra.konsumgandalf.osmPlanet.classes.enums.RoadTypes;
import com.simra.konsumgandalf.osmPlanet.classes.mapper.ZoomDistanceMapper;
import com.simra.konsumgandalf.osmPlanet.classes.mapper.ZoomRoadTypeMapper;
import com.simra.konsumgandalf.osmPlanet.repositories.OsmHighwayRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class OsmHighwayService {

	private final ZoomDistanceMapper zoomDistanceMapper = new ZoomDistanceMapper();

	private final ZoomRoadTypeMapper zoomRoadTypeMapper = new ZoomRoadTypeMapper();

	@Autowired
	private OsmHighwayRepository osmHighwayRepository;

	public List<Map<String, Object>> getHighwayInformation(double lat, double lng, int zoom) {
		Integer distanceFilter = zoomDistanceMapper.getDistanceForZoom(zoom);
		List<String> roadTypes = zoomRoadTypeMapper.getRoadTypes(zoom).stream().map(RoadTypes::getType).toList();

		List<Map<String, Object>> result = osmHighwayRepository.findHighways(lng, lat, distanceFilter, roadTypes, 0.01);
		return result;
	}

}
