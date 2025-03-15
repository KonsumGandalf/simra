package com.simra.konsumgandalf.osmPlanet.services;

import com.simra.konsumgandalf.common.models.entities.SafetyMetricsPlanetOsmLine;
import com.simra.konsumgandalf.common.models.entities.SafetyMetricsRegion;
import com.simra.konsumgandalf.common.models.entities.SafetyMetricsSimraRegion;
import com.simra.konsumgandalf.common.models.enums.TrafficTimes;
import com.simra.konsumgandalf.common.models.enums.WeekDays;
import com.simra.konsumgandalf.osmPlanet.classes.dtos.SafetyMetricsLineDTO;
import com.simra.konsumgandalf.osmPlanet.classes.dtos.SafetyMetricsRegionDTO;
import com.simra.konsumgandalf.osmPlanet.classes.specifications.SafetyMetricsGenericSpecification;
import com.simra.konsumgandalf.osmPlanet.classes.specifications.SafetyMetricsLineSpecification;
import com.simra.konsumgandalf.osmPlanet.repositories.SafetyMetricsPlanetOsmLineRepository;
import com.simra.konsumgandalf.osmPlanet.repositories.SafetyMetricsRegionRepository;
import com.simra.konsumgandalf.osmPlanet.repositories.SafetyMetricsSimraRegionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SafetyMetricsService {

	@Autowired
	private SafetyMetricsPlanetOsmLineRepository safetyMetricsRepository;

	@Autowired
	private SafetyMetricsRegionRepository safetyMetricsRegionRepository;

	@Autowired
	private SafetyMetricsSimraRegionRepository safetyMetricsSimraRegionRepository;

	public Optional<SafetyMetricsPlanetOsmLine> getSafetyMetricsOfStreet(long id, TrafficTimes trafficTime,
			WeekDays weekDay, int year) {
		return safetyMetricsRepository.findByStreetId(id, trafficTime, weekDay, year);
	}

	public Page<SafetyMetricsLineDTO> getFilteredData(Long id, String name, List<String> highwayType,
			Float minDangerousScore, Float maxDangerousScore, Integer minNumberOfRides, Integer minNumberOfIncidents,
			List<TrafficTimes> trafficTime, List<WeekDays> weekDay, List<Integer> year, Pageable pageable) {

		Specification<SafetyMetricsPlanetOsmLine> spec = SafetyMetricsGenericSpecification.filterBy("planetOsmLine", id,
				name, highwayType, minDangerousScore, maxDangerousScore, minNumberOfRides, minNumberOfIncidents,
				trafficTime, weekDay, year);

		return safetyMetricsRepository.findAll(spec, pageable)
			.map(safetyMetrics -> new SafetyMetricsLineDTO(safetyMetrics.getPlanetOsmLine().getId(),
					safetyMetrics.getPlanetOsmLine().getName(), safetyMetrics.getPlanetOsmLine().getHighway(),
					safetyMetrics.getPlanetOsmLine().getWay(), safetyMetrics.getDangerousScore(),
					safetyMetrics.getDangerousColor(), safetyMetrics.getNumberOfRides(),
					safetyMetrics.getNumberOfIncidents(), safetyMetrics.getTrafficTime(), safetyMetrics.getWeekDay(),
					safetyMetrics.getYear()));
	}

	public Page<SafetyMetricsRegionDTO> getRegionMetrics(String name, Float minDangerousScore, Integer minNumberOfRides,
			Integer minNumberOfIncidents, List<TrafficTimes> trafficTime, List<WeekDays> weekDay, List<Integer> year,
			Pageable pageable) {
		Specification<SafetyMetricsRegion> spec = SafetyMetricsGenericSpecification.filterBy("region", name,
				minDangerousScore, minNumberOfRides, minNumberOfIncidents, trafficTime, weekDay, year);

		return safetyMetricsRegionRepository.findAll(spec, pageable)
			.map(safetyMetrics -> new SafetyMetricsRegionDTO(safetyMetrics.getRegion().getName(),
					safetyMetrics.getDangerousScore(), safetyMetrics.getDangerousColor(),
					safetyMetrics.getNumberOfRides(), safetyMetrics.getNumberOfIncidents(),
					safetyMetrics.getTrafficTime(), safetyMetrics.getWeekDay(), safetyMetrics.getYear()));
	}

	public Page<SafetyMetricsRegionDTO> getSimraRegionMetrics(String name, Float minDangerousScore,
			Integer minNumberOfRides, Integer minNumberOfIncidents, List<TrafficTimes> trafficTime,
			List<WeekDays> weekDay, List<Integer> year, Pageable pageable) {
		Specification<SafetyMetricsSimraRegion> spec = SafetyMetricsGenericSpecification.filterBy("region", name,
				minDangerousScore, minNumberOfRides, minNumberOfIncidents, trafficTime, weekDay, year);

		return safetyMetricsSimraRegionRepository.findAll(spec, pageable)
			.map(safetyMetrics -> new SafetyMetricsRegionDTO(safetyMetrics.getRegion().getName(),
					safetyMetrics.getDangerousScore(), safetyMetrics.getDangerousColor(),
					safetyMetrics.getNumberOfRides(), safetyMetrics.getNumberOfIncidents(),
					safetyMetrics.getTrafficTime(), safetyMetrics.getWeekDay(), safetyMetrics.getYear()));
	}

	public List<SafetyMetricsRegion> getRegionSafetyMetrics(String name) {
		return safetyMetricsRegionRepository.findByName(name);
	}

	public List<SafetyMetricsSimraRegion> getSimraRegionSafetyMetrics(String name) {
		return safetyMetricsSimraRegionRepository.findByName(name);
	}

}
