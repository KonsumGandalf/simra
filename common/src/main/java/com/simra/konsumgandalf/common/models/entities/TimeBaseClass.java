package com.simra.konsumgandalf.common.models.entities;

import com.simra.konsumgandalf.common.models.enums.TrafficTimes;
import com.simra.konsumgandalf.common.models.enums.WeekDays;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.MappedSuperclass;
import org.checkerframework.checker.units.qual.Time;

@MappedSuperclass
public class TimeBaseClass {

	@Column(length = 21, nullable = true)
	@Enumerated(EnumType.STRING)
	private TrafficTimes trafficTime;

	@Column(length = 12, nullable = true)
	@Enumerated(EnumType.STRING)
	private WeekDays weekDay;

	protected TimeBaseClass() {
	}

	protected TimeBaseClass(TrafficTimes trafficTime, WeekDays weekDay) {
		this.trafficTime = trafficTime;
		this.weekDay = weekDay;
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

}
