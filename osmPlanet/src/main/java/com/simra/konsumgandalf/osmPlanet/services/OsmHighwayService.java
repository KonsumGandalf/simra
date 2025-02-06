package com.simra.konsumgandalf.osmPlanet.services;

import com.simra.konsumgandalf.common.models.enums.TrafficTimes;
import com.simra.konsumgandalf.common.models.enums.WeekDays;
import com.simra.konsumgandalf.osmPlanet.classes.dtos.FindNumberOfRidesWithinStreetSegmentInTimePeriodDTO;
import com.simra.konsumgandalf.osmPlanet.classes.enums.RoadTypes;
import com.simra.konsumgandalf.osmPlanet.classes.mapper.ZoomDistanceMapper;
import com.simra.konsumgandalf.osmPlanet.classes.mapper.ZoomRoadTypeMapper;
import com.simra.konsumgandalf.osmPlanet.repositories.OsmHighwayRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class OsmHighwayService {

	private final ZoomDistanceMapper zoomDistanceMapper = new ZoomDistanceMapper();

	private final ZoomRoadTypeMapper zoomRoadTypeMapper = new ZoomRoadTypeMapper();

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Autowired
	private OsmHighwayRepository osmHighwayRepository;

	public List<Map<String, Object>> getHighwayInformation(double lat, double lng, int zoom, TrafficTimes trafficTime,
			WeekDays weekDay) {
		Integer distanceFilter = zoomDistanceMapper.getDistanceForZoom(zoom);
		List<String> roadTypes = zoomRoadTypeMapper.getRoadTypes(zoom).stream().map(RoadTypes::getType).toList();

		return osmHighwayRepository.findHighways(lng, lat, distanceFilter, roadTypes, 0.0001, trafficTime.name(),
				weekDay.name());
	}

}
