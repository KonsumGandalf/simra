package com.simra.konsumgandalf.common.models.classes;

import com.simra.konsumgandalf.common.models.entities.TimeBaseClass;
import com.simra.konsumgandalf.common.models.enums.TrafficTimes;
import com.simra.konsumgandalf.common.models.enums.WeekDays;

public class SafetyMetricsKey extends TimeBaseClass {

	private Long planetOsmLineId;

	protected SafetyMetricsKey() {
		super();
	}

	protected SafetyMetricsKey(Long planetOsmLineId, TrafficTimes trafficTime, WeekDays weekDay) {
		super(trafficTime, weekDay);
		this.planetOsmLineId = planetOsmLineId;
	}

	public Long getPlanetOsmLineId() {
		return planetOsmLineId;
	}

	public void setPlanetOsmLineId(Long planetOsmLineId) {
		this.planetOsmLineId = planetOsmLineId;
	}

}
