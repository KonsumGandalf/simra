package com.simra.konsumgandalf.backend.services;

import com.simra.konsumgandalf.common.constants.CronExpressions;
import com.simra.konsumgandalf.osmPlanet.services.AnalyticsServiceHighwayMetrics;
import com.simra.konsumgandalf.osmPlanet.services.AnalyticsServiceRegionMetrics;
import com.simra.konsumgandalf.osmPlanet.services.AnalyticsServiceSimraRegionMetrics;
import com.simra.konsumgandalf.profiles.services.AnalyticsProfileService;
import com.simra.konsumgandalf.profiles.services.ProfileService;
import com.simra.konsumgandalf.rides.services.RideEntityService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class SchedulingService {

	@Autowired
	private RideEntityService rideEntityService;

	@Autowired
	private AnalyticsServiceHighwayMetrics analyticsServiceHighwayMetrics;

	@Autowired
	private AnalyticsServiceRegionMetrics analyticsServiceRegionMetrics;

	@Autowired
	private AnalyticsServiceSimraRegionMetrics analyticsServiceSimraRegionMetrics;

	@Autowired
	private ProfileService profileService;

	@Autowired
	private AnalyticsProfileService analyticsProfileService;

	@Scheduled(cron = CronExpressions.EVERY_DAY)
	public void readNewRidesAndCalculateSafetyMetrics() {
		rideEntityService.loadAllPreviousRides();

		analyticsServiceHighwayMetrics.calculateSafetyMetricsHighway();
		analyticsServiceRegionMetrics.calculateSafetyMetricsRegion();
		analyticsServiceSimraRegionMetrics.calculateSafetyMetricsSimraRegion();
	}

	@Scheduled(cron = CronExpressions.EVERY_DAY)
	public void readNewProfilesAndCalculateSafetyMetrics() {
		profileService.loadAllPrevProfiles();
		analyticsProfileService.calculateProfileSafetyMetrics();
	}

	@Async
	@EventListener(ApplicationReadyEvent.class)
	public void init() {
		if (rideEntityService.isEmpty()) {
			readNewRidesAndCalculateSafetyMetrics();
		}
		if (profileService.isEmpty()) {
			readNewProfilesAndCalculateSafetyMetrics();
		}
	}

}
