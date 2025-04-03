package com.simra.konsumgandalf.osmPlanet.utils;

import com.simra.konsumgandalf.common.models.enums.TrafficTimes;
import com.simra.konsumgandalf.common.models.enums.WeekDays;
import com.simra.konsumgandalf.osmPlanet.classes.dtos.RideEntityMetricsDTO;
import com.simra.konsumgandalf.osmPlanet.classes.dtos.RideEntityTotalDTO;
import com.simra.konsumgandalf.osmPlanet.classes.dtos.TimeFilters;

import java.util.List;
import java.util.Objects;

import static com.simra.konsumgandalf.osmPlanet.utils.TimeFilterUtils.getTimeFilters;

public final class AnalyticsUtils {

	public static RideEntityTotalDTO totalRideMetersPerRegion(List<RideEntityMetricsDTO> rideEntityMetricsDTOS,
			String regionName, TrafficTimes time, WeekDays weekDay, Integer year) {
		TimeFilters timeFilters = getTimeFilters(time, weekDay, year);

		List<RideEntityMetricsDTO> matchingRides = rideEntityMetricsDTOS.stream()
			.filter(ride -> Objects.equals(ride.getName(), regionName))
			.filter(ride -> timeFilters.trafficTimes().contains(ride.getTrafficTime()))
			.filter(ride -> timeFilters.weekDays().contains(ride.getWeekDay()))
			.filter(ride -> timeFilters.years().contains(ride.getYear().intValue()))
			.toList();

		long totalRides = matchingRides.stream().mapToLong(RideEntityMetricsDTO::getTotalRides).sum();
		float totalDistance = (float) matchingRides.stream().mapToDouble(RideEntityMetricsDTO::getTotalDistance).sum();

		return new RideEntityTotalDTO(totalRides, totalDistance);
	}

}
