package com.simra.konsumgandalf.osmPlanet.classes.dtos;

import com.simra.konsumgandalf.common.models.enums.TrafficTimes;
import com.simra.konsumgandalf.common.models.enums.WeekDays;

public interface RideEntityMetricsDTO {

	String getName();

	TrafficTimes getTrafficTime();

	WeekDays getWeekDay();

	Long getYear();

	Long getTotalRides();

	Double getTotalDistance();

}
