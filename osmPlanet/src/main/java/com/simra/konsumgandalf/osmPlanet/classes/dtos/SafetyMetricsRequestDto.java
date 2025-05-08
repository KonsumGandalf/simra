package com.simra.konsumgandalf.osmPlanet.classes.dtos;

import com.simra.konsumgandalf.common.models.enums.TrafficTimes;
import com.simra.konsumgandalf.common.models.enums.WeekDays;

import java.util.List;

public record SafetyMetricsRequestDto(List<Long> osmIds, TrafficTimes trafficTime, WeekDays weekDay, int year) {
}
