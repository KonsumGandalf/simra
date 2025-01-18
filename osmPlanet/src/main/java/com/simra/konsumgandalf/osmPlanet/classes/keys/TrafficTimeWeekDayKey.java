package com.simra.konsumgandalf.osmPlanet.classes.keys;

import com.simra.konsumgandalf.common.models.enums.TrafficTimes;
import com.simra.konsumgandalf.common.models.enums.WeekDays;

import java.util.Objects;

public class TrafficTimeWeekDayKey {

	protected final TrafficTimes trafficTime;

	protected final WeekDays weekDay;

	public TrafficTimeWeekDayKey(TrafficTimes trafficTime, WeekDays weekDay) {
		this.trafficTime = trafficTime;
		this.weekDay = weekDay;
	}

	public TrafficTimes getTrafficTime() {
		return trafficTime;
	}

	public WeekDays getWeekDay() {
		return weekDay;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		TrafficTimeWeekDayKey that = (TrafficTimeWeekDayKey) o;
		return trafficTime == that.trafficTime && weekDay == that.weekDay;
	}

	@Override
	public int hashCode() {
		return Objects.hash(trafficTime, weekDay);
	}

}
