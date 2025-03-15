package com.simra.konsumgandalf.common.models.classes;

import com.simra.konsumgandalf.common.models.entities.TimeBaseClass;
import com.simra.konsumgandalf.common.models.enums.TrafficTimes;
import com.simra.konsumgandalf.common.models.enums.WeekDays;
import jakarta.persistence.Column;
import jakarta.persistence.Id;

public class SafetyMetricsNameKey extends TimeBaseClass {

	@Id
	@Column
	private String name;

	protected SafetyMetricsNameKey() {
		super();
	}

	protected SafetyMetricsNameKey(String name, TrafficTimes trafficTime, WeekDays weekDay, Integer year) {
		super(trafficTime, weekDay, year);
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
