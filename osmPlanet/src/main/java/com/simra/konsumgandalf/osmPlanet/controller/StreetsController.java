package com.simra.konsumgandalf.osmPlanet.controller;

import com.simra.konsumgandalf.common.models.enums.TrafficTimes;
import com.simra.konsumgandalf.common.models.enums.WeekDays;
import com.simra.konsumgandalf.osmPlanet.services.OsmHighwayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("streets")
@Validated
public class StreetsController {

	@Autowired
	private OsmHighwayService osmHighwayService;

	@GetMapping("/grid")
	public List<Map<String, Object>> getHighwayInformation(@RequestParam("lat") double lat,
															   @RequestParam("lng") double lng, @RequestParam("zoom") int zoom,
															   @RequestParam(defaultValue = "ALL_DAY") TrafficTimes trafficTime,
															   @RequestParam(defaultValue = "ALL_WEEK") WeekDays weekDay) {
		return osmHighwayService.getHighwayInformation(lat, lng, zoom, trafficTime, weekDay);
	}
}
