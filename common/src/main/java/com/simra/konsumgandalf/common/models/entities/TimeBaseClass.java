package com.simra.konsumgandalf.common.models.entities;

import com.simra.konsumgandalf.common.models.enums.TrafficTimes;
import com.simra.konsumgandalf.common.models.enums.WeekDays;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import org.checkerframework.checker.units.qual.Time;

@MappedSuperclass
public class TimeBaseClass {

	@Column(length = 21, nullable = true)
	@Enumerated(EnumType.STRING)
	private TrafficTimes trafficTime;

	@Column(length = 12, nullable = true)
	@Enumerated(EnumType.STRING)
	private WeekDays weekDay;

	@Column(nullable = true)
	private Integer year;

	protected TimeBaseClass() {
	}

	protected TimeBaseClass(TrafficTimes trafficTime, WeekDays weekDay, Integer year) {
		this.trafficTime = trafficTime;
		this.weekDay = weekDay;
		this.year = year;
	}

	public TrafficTimes getTrafficTime() {
		return trafficTime;
	}

	public void setTrafficTime(TrafficTimes trafficTime) {
		this.trafficTime = trafficTime;
	}

	public WeekDays getWeekDay() {
		return weekDay;
	}

	public void setWeekDay(WeekDays weekDay) {
		this.weekDay = weekDay;
	}

	public Integer getYear() {
		return year;
	}

	public void setYear(Integer year) {
		this.year = year;
	}

}
