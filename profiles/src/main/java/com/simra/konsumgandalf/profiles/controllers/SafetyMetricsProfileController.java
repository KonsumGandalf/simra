package com.simra.konsumgandalf.profiles.controllers;

import com.simra.konsumgandalf.profiles.services.AnalyticsProfileService;
import com.simra.konsumgandalf.profiles.services.ProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/safety-metrics/profiles")
public class SafetyMetricsProfileController {

	@Autowired
	private AnalyticsProfileService analyticsProfileService;

	@Autowired
	private ProfileService profileService;

	@PostMapping("/update")
	public void updateSafetyMetricsProfiles() {
		analyticsProfileService.calculateProfileSafetyMetrics();
	}

}
