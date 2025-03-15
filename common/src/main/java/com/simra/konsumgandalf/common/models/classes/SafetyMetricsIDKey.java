package com.simra.konsumgandalf.common.models.classes;

import com.simra.konsumgandalf.common.models.entities.TimeBaseClass;
import com.simra.konsumgandalf.common.models.enums.TrafficTimes;
import com.simra.konsumgandalf.common.models.enums.WeekDays;

public class SafetyMetricsIDKey extends TimeBaseClass {

	private Long osmId;

	protected SafetyMetricsIDKey() {
		super();
	}

	protected SafetyMetricsIDKey(Long planetOsmLineId, TrafficTimes trafficTime, WeekDays weekDay, Integer year) {
		super(trafficTime, weekDay, year);
		this.osmId = planetOsmLineId;
	}

	public Long getOsmId() {
		return osmId;
	}

	public void setOsmId(Long osmId) {
		this.osmId = osmId;
	}

}
