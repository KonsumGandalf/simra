package com.simra.konsumgandalf.osmPlanet.classes.dtos;

import com.simra.konsumgandalf.common.models.enums.TrafficTimes;
import com.simra.konsumgandalf.common.models.enums.WeekDays;

public interface RegionSafetyMetricsProjection {
	Long getOsmId();
	String getName();
	TrafficTimes getTrafficTime();
	WeekDays getWeekDay();
	Long getTotalRides();
	Long getTotalIncidents();
	Long getTotalScaryIncidents();
	Long getTotalClosePasses();
	Long getTotalPullInOuts();
	Long getTotalNearLeftRightHooks();
	Long getTotalHeadOnApproaches();
	Long getTotalTailgating();
	Long getTotalNearDoorings();
	Long getTotalObstacleDodges();
}
