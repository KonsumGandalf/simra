package com.simra.konsumgandalf.profiles.models.dtos;

import com.simra.konsumgandalf.common.models.enums.SafetyMetricsProfileGroup;

public interface AggregatedSafetyMetricsDTO {

	SafetyMetricsProfileGroup getGroupType();

	String getGroupName();

	int getTotalRides();

	int getTotalIncidents();

	int getTotalScaryIncidents();

}
