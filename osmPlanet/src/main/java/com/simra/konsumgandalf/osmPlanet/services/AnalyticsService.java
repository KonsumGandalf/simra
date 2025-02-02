package com.simra.konsumgandalf.osmPlanet.services;

import com.google.common.collect.HashBiMap;
import com.simra.konsumgandalf.common.constants.CronExpressions;
import com.simra.konsumgandalf.common.logging.LogExecutionTime;
import com.simra.konsumgandalf.common.models.entities.PlanetOsmLine;
import com.simra.konsumgandalf.common.models.entities.RideIncident;
import com.simra.konsumgandalf.common.models.entities.SafetyMetrics;
import com.simra.konsumgandalf.common.models.enums.IncidentType;
import com.simra.konsumgandalf.common.models.enums.TrafficTimes;
import com.simra.konsumgandalf.common.models.enums.WeekDays;
import com.simra.konsumgandalf.common.models.maps.DangerousScoreToColorMap;
import com.simra.konsumgandalf.osmPlanet.classes.dtos.FindNumberOfRidesWithinStreetSegmentInTimePeriodDTO;
import com.simra.konsumgandalf.osmPlanet.classes.keys.TrafficTimeWeekDayKey;
import com.simra.konsumgandalf.osmPlanet.repositories.OsmHighwayRepository;
import com.simra.konsumgandalf.osmPlanet.repositories.SafetyMetricsRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * This service provides analytics for the OSM planet.
 */
@Transactional
@Service
public class AnalyticsService {

	@Autowired
	private OsmHighwayRepository osmHighwayRepository;

	@Autowired
	private SafetyMetricsRepository safetyMetricsRepository;

	private final float SCARINESS_FACTOR = 4.4f;

	private final int PAGE_SIZE = 100;

	private static final Logger _logger = LoggerFactory.getLogger(AnalyticsService.class);

	// @Scheduled(cron = CronExpressions.EVERY_DAY)
	@Async
	@LogExecutionTime
	public void updateHighwayInformation() {
		long startTime = System.nanoTime();

		List<CompletableFuture<Void>> listOfProcessedStreets = new ArrayList<>();
		final AtomicBoolean hasMoreData = new AtomicBoolean(true);
		AtomicInteger pageCounter = new AtomicInteger(0);

		while (hasMoreData.get()) {
			final int count = pageCounter.getAndIncrement();
			List<PlanetOsmLine> fetchedStreets = osmHighwayRepository.findAllStreets(PageRequest.of(count, PAGE_SIZE));

			if (fetchedStreets.isEmpty()) {
				hasMoreData.set(false);
			}

			CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
				updateSafetyMetrics(fetchedStreets);
			});

			listOfProcessedStreets.add(future);

			// pool size is 16, so we need to wait for 16 futures to complete
			if (listOfProcessedStreets.size() >= 16) {
				_logger.info("Waiting for 16 futures to complete. Running for {} seconds.",
						(System.nanoTime() - startTime) / 1e9);
				CompletableFuture.allOf(listOfProcessedStreets.toArray(new CompletableFuture[0])).join();
				listOfProcessedStreets.clear();
			}
		}

		CompletableFuture.allOf(listOfProcessedStreets.toArray(new CompletableFuture[0])).join();
		_logger.info("All highway information updated in {} seconds.", (System.nanoTime() - startTime) / 1e9);
	}

	/**
	 * This method updates the safety metrics for a list of streets.
	 * @param streets - The list of streets to update the safety metrics for
	 */
	void updateSafetyMetrics(List<PlanetOsmLine> streets) {
		ArrayList<SafetyMetrics> safetyMetricsList = new ArrayList<SafetyMetrics>();
		for (PlanetOsmLine street : streets) {
			HashBiMap<TrafficTimeWeekDayKey, SafetyMetrics> safetyMetrics = calculateSafetyMetrics(street);

			safetyMetrics.forEach((key, safetyMetric) -> {
				safetyMetric.setPlanetOsmLine(street);
				safetyMetric.setTrafficTime(key.getTrafficTime());
				safetyMetric.setWeekDay(key.getWeekDay());
				safetyMetric.setPlanetOsmLineId(street.getId());
				safetyMetricsList.add(safetyMetric);
			});
		}

		safetyMetricsRepository.saveAll(safetyMetricsList);

		return;
	}

	/**
	 * This method calculates the dangerousness score of a street. <br>
	 * Note this method has not implemented the logic of dynamic scariness factor
	 * calculation based on cities.
	 * @param street - The street to calculate the dangerousness score for
	 */
	HashBiMap<TrafficTimeWeekDayKey, SafetyMetrics> calculateSafetyMetrics(PlanetOsmLine street) {
		HashBiMap<TrafficTimeWeekDayKey, SafetyMetrics> trafficTimesSafetyMetricsHashBiMap = HashBiMap.create();

		for (FindNumberOfRidesWithinStreetSegmentInTimePeriodDTO ele : osmHighwayRepository
			.findNumberOfRidesWithinStreetSegmentInTimePeriod(street.getId())) {
			TrafficTimeWeekDayKey key = ele.getTrafficTimeWeekDayKey();

			SafetyMetrics sm = new SafetyMetrics();
			sm.setNumberOfRides(ele.getNumberOfRides());

			trafficTimesSafetyMetricsHashBiMap.put(key, sm);
		}

		for (RideIncident incident : street.getRideIncident()) {
			TrafficTimeWeekDayKey key = new TrafficTimeWeekDayKey(incident.getTrafficTime(), incident.getWeekDay());
			SafetyMetrics safetyMetrics = trafficTimesSafetyMetricsHashBiMap.getOrDefault(key, new SafetyMetrics());

			safetyMetrics.setNumberOfIncidents(safetyMetrics.getNumberOfIncidents() + 1);
			if (incident.isScary()) {
				safetyMetrics.setNumberOfScaryIncidents(safetyMetrics.getNumberOfScaryIncidents() + 1);
			}

			switch (incident.getIncidentType()) {
				case IncidentType.PULLING_IN_OUT ->
					safetyMetrics.setNumberOfPullInOuts(safetyMetrics.getNumberOfPullInOuts() + 1);
				case IncidentType.CLOSE_PASS ->
					safetyMetrics.setNumberOfClosePasses(safetyMetrics.getNumberOfClosePasses() + 1);
				case IncidentType.NEAR_LEFT_RIGHT_HOOK ->
					safetyMetrics.setNumberOfNearLeftRightHooks(safetyMetrics.getNumberOfNearLeftRightHooks() + 1);
				case IncidentType.HEAD_ON_APPROACH ->
					safetyMetrics.setNumberOfHeadOnApproaches(safetyMetrics.getNumberOfHeadOnApproaches() + 1);
				case IncidentType.TAILGATING ->
					safetyMetrics.setNumberOfTailgating(safetyMetrics.getNumberOfTailgating() + 1);
				case IncidentType.NEAR_DOORING ->
					safetyMetrics.setNumberOfNearDoorings(safetyMetrics.getNumberOfNearDoorings() + 1);
				case IncidentType.DODGING_OBSTACLE ->
					safetyMetrics.setNumberOfObstacleDodges(safetyMetrics.getNumberOfObstacleDodges() + 1);
			}

			trafficTimesSafetyMetricsHashBiMap.put(key, safetyMetrics);
		}

		calculateAllWeekValues(trafficTimesSafetyMetricsHashBiMap);
		calculateAllDayValues(trafficTimesSafetyMetricsHashBiMap);

		trafficTimesSafetyMetricsHashBiMap.forEach((trafficTimeWeekDayKey, safetyMetrics) -> {
			safetyMetrics.setTrafficTime(trafficTimeWeekDayKey.getTrafficTime());
			safetyMetrics.setWeekDay(trafficTimeWeekDayKey.getWeekDay());

			int numberOfNonScaryIncidents = safetyMetrics.getNumberOfIncidents()
					- safetyMetrics.getNumberOfScaryIncidents();

			float dangerousScore;
			if (safetyMetrics.getNumberOfRides() == 0 && safetyMetrics.getNumberOfIncidents() != 0) {
				safetyMetrics.setNumberOfRides(safetyMetrics.getNumberOfIncidents());
			}
			dangerousScore = ((SCARINESS_FACTOR * safetyMetrics.getNumberOfScaryIncidents() + numberOfNonScaryIncidents)
					/ safetyMetrics.getNumberOfRides());
			safetyMetrics.setDangerousScore(dangerousScore);
			String dangerousColor = DangerousScoreToColorMap.getColorForScore(dangerousScore);
			safetyMetrics.setDangerousColor(dangerousColor);
		});

		return trafficTimesSafetyMetricsHashBiMap;
	}

	void calculateAllDayValues(HashBiMap<TrafficTimeWeekDayKey, SafetyMetrics> trafficTimesSafetyMetricsHashBiMap) {
		TrafficTimeWeekDayKey allWeekKey = new TrafficTimeWeekDayKey(TrafficTimes.ALL_DAY, WeekDays.WEEK);
		SafetyMetrics allWeekSM = new SafetyMetrics();

		TrafficTimeWeekDayKey allWeekendKey = new TrafficTimeWeekDayKey(TrafficTimes.ALL_DAY, WeekDays.WEEKEND);
		SafetyMetrics allWeekendSM = new SafetyMetrics();

		for (TrafficTimes trafficTime : TrafficTimes.values()) {
			TrafficTimeWeekDayKey weekDayKey = new TrafficTimeWeekDayKey(trafficTime, WeekDays.WEEK);
			if (trafficTimesSafetyMetricsHashBiMap.containsKey(weekDayKey)) {
				allWeekSM.addUpSafetyMetric(trafficTimesSafetyMetricsHashBiMap.get(weekDayKey));
			}

			TrafficTimeWeekDayKey weekendKey = new TrafficTimeWeekDayKey(trafficTime, WeekDays.WEEKEND);
			if (trafficTimesSafetyMetricsHashBiMap.containsKey(weekendKey)) {
				allWeekendSM.addUpSafetyMetric(trafficTimesSafetyMetricsHashBiMap.get(weekendKey));
			}
		}

		TrafficTimeWeekDayKey allWeekAllDayKey = new TrafficTimeWeekDayKey(TrafficTimes.ALL_DAY, WeekDays.ALL_WEEK);
		SafetyMetrics allWeekAllDaySM = new SafetyMetrics().addUpSafetyMetric(allWeekSM)
			.addUpSafetyMetric(allWeekendSM);
		allWeekAllDaySM.setTrafficTime(allWeekAllDayKey.getTrafficTime());
		allWeekAllDaySM.setWeekDay(allWeekAllDayKey.getWeekDay());

		trafficTimesSafetyMetricsHashBiMap.put(allWeekKey, allWeekSM);
		trafficTimesSafetyMetricsHashBiMap.put(allWeekendKey, allWeekendSM);
		trafficTimesSafetyMetricsHashBiMap.put(allWeekAllDayKey, allWeekAllDaySM);
	}

	void calculateAllWeekValues(HashBiMap<TrafficTimeWeekDayKey, SafetyMetrics> trafficTimesSafetyMetricsHashBiMap) {
		SafetyMetrics allEarlyRushHour = new SafetyMetrics();
		SafetyMetrics allMidDay = new SafetyMetrics();
		SafetyMetrics allLateRushHour = new SafetyMetrics();
		SafetyMetrics allNight = new SafetyMetrics();
		for (WeekDays weekDays : WeekDays.values()) {
			TrafficTimeWeekDayKey earlyRushHourKey = new TrafficTimeWeekDayKey(TrafficTimes.MORNING_RUSH_HOUR,
					weekDays);
			if (trafficTimesSafetyMetricsHashBiMap.containsKey(earlyRushHourKey)) {
				allEarlyRushHour.addUpSafetyMetric(trafficTimesSafetyMetricsHashBiMap.get(earlyRushHourKey));
			}

			TrafficTimeWeekDayKey midDayKey = new TrafficTimeWeekDayKey(TrafficTimes.MID_DAY, weekDays);
			if (trafficTimesSafetyMetricsHashBiMap.containsKey(midDayKey)) {
				allMidDay.addUpSafetyMetric(trafficTimesSafetyMetricsHashBiMap.get(midDayKey));
			}

			TrafficTimeWeekDayKey lateRushHourKey = new TrafficTimeWeekDayKey(TrafficTimes.EVENING_RUSH_HOUR, weekDays);
			if (trafficTimesSafetyMetricsHashBiMap.containsKey(lateRushHourKey)) {
				allLateRushHour.addUpSafetyMetric(trafficTimesSafetyMetricsHashBiMap.get(lateRushHourKey));
			}

			TrafficTimeWeekDayKey nightKey = new TrafficTimeWeekDayKey(TrafficTimes.EVENING_NIGHT_MORNING, weekDays);
			if (trafficTimesSafetyMetricsHashBiMap.containsKey(nightKey)) {
				allNight.addUpSafetyMetric(trafficTimesSafetyMetricsHashBiMap.get(nightKey));
			}
		}

		trafficTimesSafetyMetricsHashBiMap
			.put(new TrafficTimeWeekDayKey(TrafficTimes.MORNING_RUSH_HOUR, WeekDays.ALL_WEEK), allEarlyRushHour);
		trafficTimesSafetyMetricsHashBiMap.put(new TrafficTimeWeekDayKey(TrafficTimes.MID_DAY, WeekDays.ALL_WEEK),
				allMidDay);
		trafficTimesSafetyMetricsHashBiMap
			.put(new TrafficTimeWeekDayKey(TrafficTimes.EVENING_RUSH_HOUR, WeekDays.ALL_WEEK), allLateRushHour);
		trafficTimesSafetyMetricsHashBiMap
			.put(new TrafficTimeWeekDayKey(TrafficTimes.EVENING_NIGHT_MORNING, WeekDays.ALL_WEEK), allNight);
	}

}
