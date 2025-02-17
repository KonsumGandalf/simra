package com.simra.konsumgandalf.osmPlanet.classes.dtos;

import com.simra.konsumgandalf.common.models.enums.TrafficTimes;
import com.simra.konsumgandalf.common.models.enums.WeekDays;
import org.locationtech.jts.geom.Geometry;

public class SafetyMetricsDTO {
	private final Long id;
	private final String name;
	private final String highway;
	private final float dangerousScore;
	private final String dangerousColor;
	private final int numberOfRides;
	private final int numberOfIncidents;
	private final TrafficTimes trafficTime;
	private final WeekDays weekDay;
	private final Geometry way;

	public SafetyMetricsDTO(Long id, String name, String highway, Geometry way, float dangerousScore, String dangerousColor, int numberOfRides, int numberOfIncidents, TrafficTimes trafficTime, WeekDays weekDay) {
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
}
