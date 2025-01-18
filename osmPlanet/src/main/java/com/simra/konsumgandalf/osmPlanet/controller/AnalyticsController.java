package com.simra.konsumgandalf.osmPlanet.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.simra.konsumgandalf.osmPlanet.services.AnalyticsService;

/**
 * Temporary controller to update the highway information
 */
@RestController
@RequestMapping("analytics")
public class AnalyticsController {

	@Autowired
	private AnalyticsService analyticsService;

	@PostMapping("/update")
	public void updateHighwayInformation() {
		analyticsService.updateHighwayInformation();
	}

}
