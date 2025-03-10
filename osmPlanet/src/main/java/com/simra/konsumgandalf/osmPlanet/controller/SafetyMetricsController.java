package com.simra.konsumgandalf.osmPlanet.controller;

import com.simra.konsumgandalf.common.models.entities.SafetyMetricsPlanetOsmLine;
import com.simra.konsumgandalf.common.models.entities.SafetyMetricsRegion;
import com.simra.konsumgandalf.common.models.entities.SafetyMetricsSimraRegion;
import com.simra.konsumgandalf.common.models.enums.TrafficTimes;
import com.simra.konsumgandalf.common.models.enums.WeekDays;
import com.simra.konsumgandalf.osmPlanet.classes.dtos.SafetyMetricsDTO;
import com.simra.konsumgandalf.osmPlanet.services.SafetyMetricsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("safety-metrics")
public class SafetyMetricsController {

	@Autowired
	private SafetyMetricsService safetyMetricsService;

	@GetMapping("streets/{id}")
	public Optional<SafetyMetricsPlanetOsmLine> getSafetyDetailsOfStreet(@PathVariable long id,
																		 @RequestParam(defaultValue = "ALL_DAY") TrafficTimes trafficTime,
																		 @RequestParam(defaultValue = "ALL_WEEK") WeekDays weekDay) {
		return safetyMetricsService.getSafetyMetricsOfStreet(id, trafficTime, weekDay);
	}

	@GetMapping("/streets")
	public Page<SafetyMetricsDTO> getFilteredData(@RequestParam(required = false) Long id,
			@RequestParam(required = false) String name, @RequestParam(required = false) List<String> highway,
			@RequestParam(required = false) Float minDangerousScore,
			@RequestParam(required = false) Float maxDangerousScore,
			@RequestParam(required = false) Integer minNumberOfRides,
			@RequestParam(required = false) Integer minNumberOfIncidents,
			@RequestParam(required = false) List<TrafficTimes> trafficTime,
			@RequestParam(required = false) List<WeekDays> weekDay, Pageable pageable) {
		return safetyMetricsService.getFilteredData(id, name, highway, minDangerousScore, maxDangerousScore,
				minNumberOfRides, minNumberOfIncidents, trafficTime, weekDay, pageable);
	}

	@GetMapping("/region/{name}")
	public Optional<SafetyMetricsRegion> getRegionSafetyMetrics(@PathVariable String name) {
		return safetyMetricsService.getRegionSafetyMetrics(name);
	}

	@GetMapping("/simra-region/{name}")
	public Optional<SafetyMetricsSimraRegion> getSimraRegionSafetyMetrics(@PathVariable String name) {
		return safetyMetricsService.getSimraRegionSafetyMetrics(name);
	}

}
