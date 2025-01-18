package com.simra.konsumgandalf.osmPlanet.controller;

import com.simra.konsumgandalf.common.models.entities.SafetyMetrics;
import com.simra.konsumgandalf.common.models.enums.TrafficTimes;
import com.simra.konsumgandalf.common.models.enums.WeekDays;
import com.simra.konsumgandalf.osmPlanet.services.SafetyMetricsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("safety-metrics")
public class SafetyMetricsController {

	@Autowired
	private SafetyMetricsService safetyMetricsService;

	@GetMapping("streets/{id}")
	public Optional<SafetyMetrics> getSafetyDetailsOfStreet(@PathVariable long id,
			@RequestParam(defaultValue = "ALL_DAY") TrafficTimes trafficTime,
			@RequestParam(defaultValue = "ALL_WEEK") WeekDays weekDay) {
		return safetyMetricsService.getSafetyMetricsOfStreet(id, trafficTime, weekDay);
	}

}
