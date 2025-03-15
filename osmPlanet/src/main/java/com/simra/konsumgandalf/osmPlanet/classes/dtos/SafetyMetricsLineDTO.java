package com.simra.konsumgandalf.osmPlanet.classes.dtos;

import com.simra.konsumgandalf.common.models.enums.TrafficTimes;
import com.simra.konsumgandalf.common.models.enums.WeekDays;
import org.locationtech.jts.geom.Geometry;

public class SafetyMetricsLineDTO {

	private final Long id;

	private final String name;

	private final String highway;

	private final float dangerousScore;

	private final String dangerousColor;

	private final int numberOfRides;

	private final int numberOfIncidents;

	private final TrafficTimes trafficTime;

	private final WeekDays weekDay;

	private final Integer year;

	private final Geometry way;

	public SafetyMetricsLineDTO(Long id, String name, String highway, Geometry way, float dangerousScore,
			String dangerousColor, int numberOfRides, int numberOfIncidents, TrafficTimes trafficTime, WeekDays weekDay,
			Integer year) {
		this.id = id;
		this.name = name;
		this.highway = highway;
		this.way = way;
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

	public String getHighway() {
		return highway;
	}

	public Long getId() {
		return id;
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

	public Geometry getWay() {
		return way;
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
