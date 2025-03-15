package com.simra.konsumgandalf.osmPlanet.controller;

import com.simra.konsumgandalf.osmPlanet.services.AnalyticsServiceRegionMetrics;
import com.simra.konsumgandalf.osmPlanet.services.AnalyticsServiceSimraRegionMetrics;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.simra.konsumgandalf.osmPlanet.services.AnalyticsServiceHighwayMetrics;

/**
 * Temporary controller to update the highway information
 */
@RestController
@RequestMapping("analytics")
public class AnalyticsController {

	@Autowired
	private AnalyticsServiceHighwayMetrics analyticsServiceHighwayMetrics;

	@Autowired
	private AnalyticsServiceRegionMetrics analyticsServiceRegionMetrics;

	@Autowired
	private AnalyticsServiceSimraRegionMetrics analyticsServiceSimraRegionMetrics;

	@PostMapping("/update/highway")
	public void updateHighwayInformation() {
		analyticsServiceHighwayMetrics.updateSafetyMetricsHighway();
	}

	@PostMapping("/update/region")
	public void updateRegionSafetyMetrics() {
		analyticsServiceRegionMetrics.calculateSafetyMetricsRegion();
	}

	@PostMapping("/update/simra-region")
	public void updateSimraRegionSafetyMetrics() {
		analyticsServiceSimraRegionMetrics.calculateSafetyMetricsSimraRegion();
	}

}
