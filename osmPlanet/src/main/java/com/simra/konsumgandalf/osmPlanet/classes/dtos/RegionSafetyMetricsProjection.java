package com.simra.konsumgandalf.osmPlanet.classes.dtos;

import com.simra.konsumgandalf.common.models.enums.TrafficTimes;
import com.simra.konsumgandalf.common.models.enums.WeekDays;

public interface RegionSafetyMetricsProjection {

	Long getOsmId();

	String getName();

	Long getAdminLevel();

	TrafficTimes getTrafficTime();

	WeekDays getWeekDay();

	Integer getYear();

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
