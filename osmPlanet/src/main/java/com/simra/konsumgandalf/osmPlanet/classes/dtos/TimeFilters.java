package com.simra.konsumgandalf.osmPlanet.classes.dtos;

import com.simra.konsumgandalf.common.models.enums.TrafficTimes;
import com.simra.konsumgandalf.common.models.enums.WeekDays;

import java.util.List;

public record TimeFilters(List<TrafficTimes> trafficTimes, List<WeekDays> weekDays, List<Integer> years) {
}
