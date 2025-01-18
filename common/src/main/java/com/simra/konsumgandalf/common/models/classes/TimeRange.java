package com.simra.konsumgandalf.common.models.classes;

import com.simra.konsumgandalf.common.models.enums.TrafficTimes;

import java.time.LocalTime;

public class TimeRange {

	private final LocalTime start;

	private final LocalTime end;

	private final TrafficTimes trafficTime;

	public TimeRange(LocalTime start, LocalTime end, TrafficTimes trafficTime) {
		this.start = start;
		this.end = end;
		this.trafficTime = trafficTime;
	}

	public LocalTime getStart() {
		return start;
	}

	public LocalTime getEnd() {
		return end;
	}

	public TrafficTimes getTrafficTime() {
		return trafficTime;
	}

}
