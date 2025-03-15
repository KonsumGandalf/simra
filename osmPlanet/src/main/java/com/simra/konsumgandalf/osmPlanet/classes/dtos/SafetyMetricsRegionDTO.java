package com.simra.konsumgandalf.osmPlanet.classes.dtos;

import com.simra.konsumgandalf.common.models.enums.TrafficTimes;
import com.simra.konsumgandalf.common.models.enums.WeekDays;
import org.locationtech.jts.geom.Geometry;

public class SafetyMetricsRegionDTO {

	private final String name;

	private final float dangerousScore;

	private final String dangerousColor;

	private final int numberOfRides;

	private final int numberOfIncidents;

	private final TrafficTimes trafficTime;

	private final WeekDays weekDay;

	private final Integer year;

	public SafetyMetricsRegionDTO(String name, float dangerousScore, String dangerousColor, int numberOfRides,
			int numberOfIncidents, TrafficTimes trafficTime, WeekDays weekDay, Integer year) {
		this.name = name;
		this.dangerousScore = dangerousScore;
		this.dangerousColor = dangerousColor;
		this.numberOfRides = numberOfRides;
		this.numberOfIncidents = numberOfIncidents;
		this.trafficTime = trafficTime;
		this.weekDay = weekDay;
		this.year = year;
	}

	public String getName() {
		return name;
	}

	public float getDangerousScore() {
		return dangerousScore;
	}

	public String getDangerousColor() {
		return dangerousColor;
	}

	public int getNumberOfRides() {
		return numberOfRides;
	}

	public int getNumberOfIncidents() {
		return numberOfIncidents;
	}

	public TrafficTimes getTrafficTime() {
		return trafficTime;
	}

	public WeekDays getWeekDay() {
		return weekDay;
	}

	public Integer getYear() {
		return year;
	}

}
