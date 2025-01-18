package com.simra.konsumgandalf.osmPlanet.services;

import com.simra.konsumgandalf.common.models.entities.SafetyMetrics;
import com.simra.konsumgandalf.common.models.enums.TrafficTimes;
import com.simra.konsumgandalf.common.models.enums.WeekDays;
import com.simra.konsumgandalf.osmPlanet.repositories.SafetyMetricsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class SafetyMetricsService {

	@Autowired
	private SafetyMetricsRepository safetyMetricsRepository;

	public Optional<SafetyMetrics> getSafetyMetricsOfStreet(long id, TrafficTimes trafficTime, WeekDays weekDay) {
		Optional<SafetyMetrics> t = safetyMetricsRepository.findByStreetId(id, trafficTime, weekDay);
		return t;
	}

}
