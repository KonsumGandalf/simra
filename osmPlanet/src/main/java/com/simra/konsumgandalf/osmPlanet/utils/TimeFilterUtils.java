package com.simra.konsumgandalf.osmPlanet.utils;

import com.google.common.collect.HashBiMap;
import com.simra.konsumgandalf.common.models.entities.SafetyMetricsPlanetOsmLine;
import com.simra.konsumgandalf.common.models.enums.TrafficTimes;
import com.simra.konsumgandalf.common.models.enums.WeekDays;
import com.simra.konsumgandalf.osmPlanet.classes.dtos.TimeFilters;
import com.simra.konsumgandalf.osmPlanet.classes.keys.TrafficTimeWeekDayKey;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.stream.IntStream;

public final class TimeFilterUtils {

	public static TimeFilters getTimeFilters(TrafficTimes time, WeekDays weekDay, Integer year) {
		List<TrafficTimes> trafficTimes;
		if (time == TrafficTimes.ALL_DAY) {
			trafficTimes = List.of(TrafficTimes.MORNING_RUSH_HOUR, TrafficTimes.MID_DAY, TrafficTimes.EVENING_RUSH_HOUR,
					TrafficTimes.EVENING_NIGHT_MORNING);
		}
		else {
			trafficTimes = List.of(time);
		}

		List<WeekDays> weekDays;
		if (weekDay == WeekDays.ALL_WEEK) {
			weekDays = List.of(WeekDays.WEEK, WeekDays.WEEKEND);
		}
		else {
			weekDays = List.of(weekDay);
		}

		List<Integer> years;
		if (year == 2000) {
			int currentYear = new GregorianCalendar().get(Calendar.YEAR);
			years = IntStream.rangeClosed(2018, currentYear).boxed().toList();
		}
		else {
			years = List.of(year);
		}

		return new TimeFilters(trafficTimes, weekDays, years);
	}

	public static List<Integer> getAllYears(HashBiMap<TrafficTimeWeekDayKey, SafetyMetricsPlanetOsmLine> metricsMap) {
		return metricsMap.keySet()
			.stream()
			.map(TrafficTimeWeekDayKey::getYear)
			.filter(year -> year != 2000)
			.distinct()
			.toList();
	}

}
