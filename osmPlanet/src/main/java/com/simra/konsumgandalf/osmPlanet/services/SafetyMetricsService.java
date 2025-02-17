package com.simra.konsumgandalf.osmPlanet.services;

import com.simra.konsumgandalf.common.models.entities.SafetyMetrics;
import com.simra.konsumgandalf.common.models.enums.TrafficTimes;
import com.simra.konsumgandalf.common.models.enums.WeekDays;
import com.simra.konsumgandalf.osmPlanet.classes.dtos.SafetyMetricsDTO;
import com.simra.konsumgandalf.osmPlanet.classes.specifications.SafetyMetricsSpecification;
import com.simra.konsumgandalf.osmPlanet.repositories.SafetyMetricsRepository;
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
	private SafetyMetricsRepository safetyMetricsRepository;

	public Optional<SafetyMetrics> getSafetyMetricsOfStreet(long id, TrafficTimes trafficTime, WeekDays weekDay) {
		return safetyMetricsRepository.findByStreetId(id, trafficTime, weekDay);
	}

	public Page<SafetyMetricsDTO> getFilteredData(Long id, String name, List<String> highwayType, Float minDangerousScore, Float maxDangerousScore,
												  Integer minNumberOfRides, Integer minNumberOfIncidents,
												  List<TrafficTimes> trafficTime, List<WeekDays> weekDay, Pageable pageable) {

		Specification<SafetyMetrics> spec = SafetyMetricsSpecification.filterBy(
				id, name, highwayType, minDangerousScore, maxDangerousScore, minNumberOfRides, minNumberOfIncidents, trafficTime, weekDay);

		return safetyMetricsRepository.findAll(spec, pageable).map(safetyMetrics ->  new SafetyMetricsDTO(
			safetyMetrics.getPlanetOsmLine().getId(),
			safetyMetrics.getPlanetOsmLine().getName(),
			safetyMetrics.getPlanetOsmLine().getHighway(),
			safetyMetrics.getPlanetOsmLine().getWay(),
			safetyMetrics.getDangerousScore(),
			safetyMetrics.getDangerousColor(),
			safetyMetrics.getNumberOfRides(),
			safetyMetrics.getNumberOfIncidents(),
			safetyMetrics.getTrafficTime(),
			safetyMetrics.getWeekDay()
			)
		);

	}

}
