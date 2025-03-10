package com.simra.konsumgandalf.osmPlanet.services;

import com.google.common.collect.HashBiMap;
import com.simra.konsumgandalf.common.logging.LogExecutionTime;
import com.simra.konsumgandalf.common.models.entities.PlanetOsmLine;
import com.simra.konsumgandalf.common.models.entities.Region;
import com.simra.konsumgandalf.common.models.entities.RideIncident;
import com.simra.konsumgandalf.common.models.entities.SafetyMetrics;
import com.simra.konsumgandalf.common.models.entities.SafetyMetricsPlanetOsmLine;
import com.simra.konsumgandalf.common.models.entities.SafetyMetricsRegion;
import com.simra.konsumgandalf.common.models.entities.SafetyMetricsSimraRegion;
import com.simra.konsumgandalf.common.models.entities.SimraRegion;
import com.simra.konsumgandalf.common.models.enums.IncidentType;
import com.simra.konsumgandalf.common.models.enums.TrafficTimes;
import com.simra.konsumgandalf.common.models.enums.WeekDays;
import com.simra.konsumgandalf.common.models.maps.DangerousScoreToColorMap;
import com.simra.konsumgandalf.osmPlanet.classes.dtos.FindNumberOfRidesWithinStreetSegmentInTimePeriodDTO;
import com.simra.konsumgandalf.osmPlanet.classes.dtos.RegionSafetyMetricsProjection;
import com.simra.konsumgandalf.osmPlanet.classes.keys.RegionTrafficTimeWeekDayKey;
import com.simra.konsumgandalf.osmPlanet.classes.keys.TrafficTimeWeekDayKey;
import com.simra.konsumgandalf.osmPlanet.classes.mapper.SimraRegionMapper;
import com.simra.konsumgandalf.osmPlanet.repositories.OsmHighwayRepository;
import com.simra.konsumgandalf.osmPlanet.repositories.RegionRepository;
import com.simra.konsumgandalf.osmPlanet.repositories.SafetyMetricsPlanetOsmLineRepository;
import com.simra.konsumgandalf.osmPlanet.repositories.SafetyMetricsRegionRepository;
import com.simra.konsumgandalf.osmPlanet.repositories.SafetyMetricsSimraRegionRepository;
import com.simra.konsumgandalf.osmPlanet.repositories.SimraRegionRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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
	private SafetyMetricsPlanetOsmLineRepository safetyMetricsLineRepository;

	@Autowired
	private SafetyMetricsRegionRepository safetyMetricsRegionRepository;

	@Autowired
	private SafetyMetricsSimraRegionRepository safetyMetricsSimraRegionRepository;

	@Autowired
	private SimraRegionRepository simraRegionRepository;

	@Autowired
	private RegionRepository regionRepository;

	private final float SCARINESS_FACTOR = 4.4f;

	private final int PAGE_SIZE = 100;

	private static final Logger _logger = LoggerFactory.getLogger(AnalyticsService.class);
	private static final SimraRegionMapper simraMapper = new SimraRegionMapper();

	// @Scheduled(cron = CronExpressions.EVERY_DAY)
	@Async
	@LogExecutionTime
	public void updateSafetyMetricsHighway() {
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

	@Async
	@LogExecutionTime
	public void calculateSafetyMetricsRegion() {
		List<RegionSafetyMetricsProjection> statesSafetyMetrics = safetyMetricsLineRepository.getRegionSafetyMetricsOfAdminLevel(4);

		List<Region> regions = statesSafetyMetrics.stream().map(
				safetyMetricsProjection -> new Region(safetyMetricsProjection.getName(), safetyMetricsProjection.getOsmId()))
				.distinct()
				.toList();
		regionRepository.saveAll(regions);

		ArrayList<SafetyMetricsRegion> safetyMetricsRegionList = new ArrayList<>();
		for (RegionSafetyMetricsProjection safetyMetricsProjection : statesSafetyMetrics) {
			Region region = regionRepository.findByName(safetyMetricsProjection.getName()).orElseThrow();

			SafetyMetricsRegion safetyMetrics = new SafetyMetricsRegion(
					safetyMetricsProjection.getTrafficTime(),
					safetyMetricsProjection.getWeekDay(),
					Math.toIntExact(safetyMetricsProjection.getTotalRides()),
					Math.toIntExact(safetyMetricsProjection.getTotalIncidents()),
					Math.toIntExact(safetyMetricsProjection.getTotalScaryIncidents()),
					Math.toIntExact(safetyMetricsProjection.getTotalClosePasses()),
					Math.toIntExact(safetyMetricsProjection.getTotalPullInOuts()),
					Math.toIntExact(safetyMetricsProjection.getTotalNearLeftRightHooks()),
					Math.toIntExact(safetyMetricsProjection.getTotalHeadOnApproaches()),
					Math.toIntExact(safetyMetricsProjection.getTotalTailgating()),
					Math.toIntExact(safetyMetricsProjection.getTotalNearDoorings()),
					Math.toIntExact(safetyMetricsProjection.getTotalObstacleDodges())
			);

			float dangerousScore = calculateDangerousScore(safetyMetrics);
			safetyMetrics.setDangerousScore(dangerousScore);

			String dangerousColor = DangerousScoreToColorMap.getColorForScore(dangerousScore);
			safetyMetrics.setDangerousColor(dangerousColor);

			safetyMetrics.setRegion(region);
			safetyMetrics.setName(region.getName());

			safetyMetricsRegionList.add(safetyMetrics);
		}
		safetyMetricsRegionRepository.saveAll(safetyMetricsRegionList);
	}

	public void calculateSafetyMetricsSimraRegion() {
		ArrayList<SimraRegion> simraRegions = new ArrayList<>();
		ArrayList<SafetyMetricsSimraRegion> safetyMetricsRegionList = new ArrayList<>();
		HashBiMap<RegionTrafficTimeWeekDayKey, SafetyMetricsRegion> metricsRegionHashBiMap = HashBiMap.create();
		for (Map.Entry<String, List<String>> entry: simraMapper.map.entrySet()) {
			SimraRegion simraRegion = simraRegionRepository.findByName(entry.getKey())
					.orElseGet(() -> new SimraRegion(entry.getKey()));

			ArrayList<Region> regions = new ArrayList<>();
			for (String regionString : entry.getValue()) {
  				Optional<Region> optionalRegion = regionRepository.findByName(regionString);

				if (optionalRegion.isEmpty()) {
					_logger.warn("State {} not found in database", regionString);
					continue;
				}

				Region region = optionalRegion.get();
				region.setSimraRegion(simraRegion);
				regions.add(region);
				for (SafetyMetricsRegion safetyMetricsRegion : region.getSafetyMetricsRegions()) {
					RegionTrafficTimeWeekDayKey key = new RegionTrafficTimeWeekDayKey(simraRegion, safetyMetricsRegion.getTrafficTime(), safetyMetricsRegion.getWeekDay());
					if (metricsRegionHashBiMap.containsKey(key)) {
						SafetyMetricsRegion existingSafetyMetricsRegion = metricsRegionHashBiMap.get(key);
						existingSafetyMetricsRegion.addUpSafetyMetric(safetyMetricsRegion);
						metricsRegionHashBiMap.put(key, existingSafetyMetricsRegion);
					} else {
						metricsRegionHashBiMap.put(key, safetyMetricsRegion);
					}
				}
			}

			if (regions.isEmpty()) {
				_logger.warn("No regions found for simra region {}", entry.getKey());
				continue;
			}

			simraRegion.setRegions(regions);
			simraRegions.add(simraRegion);
		}

		for (Map.Entry<RegionTrafficTimeWeekDayKey, SafetyMetricsRegion> entry: metricsRegionHashBiMap.entrySet()) {
			SafetyMetricsSimraRegion safetyMetricsSimraRegion = new SafetyMetricsSimraRegion(
					entry.getValue().getTrafficTime(),
					entry.getValue().getWeekDay(),
					entry.getValue().getNumberOfRides(),
					entry.getValue().getNumberOfIncidents(),
					entry.getValue().getNumberOfScaryIncidents(),
					entry.getValue().getNumberOfClosePasses(),
					entry.getValue().getNumberOfPullInOuts(),
					entry.getValue().getNumberOfNearLeftRightHooks(),
					entry.getValue().getNumberOfHeadOnApproaches(),
					entry.getValue().getNumberOfTailgating(),
					entry.getValue().getNumberOfNearDoorings(),
					entry.getValue().getNumberOfObstacleDodges()
			);

			safetyMetricsSimraRegion.setRegion(entry.getKey().getSimraRegion());
			safetyMetricsSimraRegion.setName(entry.getKey().getSimraRegion().getName());

			float dangerousScore = calculateDangerousScore(safetyMetricsSimraRegion);
			safetyMetricsSimraRegion.setDangerousScore(dangerousScore);

			String dangerousColor = DangerousScoreToColorMap.getColorForScore(dangerousScore);
			safetyMetricsSimraRegion.setDangerousColor(dangerousColor);

			safetyMetricsRegionList.add(safetyMetricsSimraRegion);
		}

		simraRegionRepository.saveAll(simraRegions);

		safetyMetricsSimraRegionRepository.saveAll(safetyMetricsRegionList);
	}


	/**
	 * This method updates the safety metrics for a list of streets.
	 * @param streets - The list of streets to update the safety metrics for
	 */
	void updateSafetyMetrics(List<PlanetOsmLine> streets) {
		ArrayList<SafetyMetricsPlanetOsmLine> safetyMetricsPlanetOsmLineList = new ArrayList<SafetyMetricsPlanetOsmLine>();
		for (PlanetOsmLine street : streets) {
			HashBiMap<TrafficTimeWeekDayKey, SafetyMetricsPlanetOsmLine> safetyMetrics = calculateSafetyMetrics(street);

			safetyMetrics.forEach((key, safetyMetric) -> {
				safetyMetric.setPlanetOsmLine(street);
				safetyMetric.setTrafficTime(key.getTrafficTime());
				safetyMetric.setWeekDay(key.getWeekDay());
				safetyMetric.setOsmId(street.getId());
				safetyMetricsPlanetOsmLineList.add(safetyMetric);
			});
		}

		safetyMetricsLineRepository.saveAll(safetyMetricsPlanetOsmLineList);

		return;
	}

	/**
	 * This method calculates the dangerousness score of a street. <br>
	 * Note this method has not implemented the logic of dynamic scariness factor
	 * calculation based on cities.
	 * @param street - The street to calculate the dangerousness score for
	 */
	HashBiMap<TrafficTimeWeekDayKey, SafetyMetricsPlanetOsmLine> calculateSafetyMetrics(PlanetOsmLine street) {
		HashBiMap<TrafficTimeWeekDayKey, SafetyMetricsPlanetOsmLine> trafficTimesSafetyMetricsHashBiMap = HashBiMap.create();

		for (FindNumberOfRidesWithinStreetSegmentInTimePeriodDTO ele : osmHighwayRepository
			.findNumberOfRidesWithinStreetSegmentInTimePeriod(street.getId())) {
			TrafficTimeWeekDayKey key = ele.getTrafficTimeWeekDayKey();

			SafetyMetricsPlanetOsmLine sm = new SafetyMetricsPlanetOsmLine();
			sm.setNumberOfRides(ele.getNumberOfRides());

			trafficTimesSafetyMetricsHashBiMap.put(key, sm);
		}

		for (RideIncident incident : street.getRideIncident()) {
			TrafficTimeWeekDayKey key = new TrafficTimeWeekDayKey(incident.getTrafficTime(), incident.getWeekDay());
			if (!trafficTimesSafetyMetricsHashBiMap.containsKey(key)) {
				_logger.warn("No safety metrics found for key: {}", key);
				continue;
			}
			SafetyMetricsPlanetOsmLine safetyMetricsPlanetOsmLine = trafficTimesSafetyMetricsHashBiMap.get(key);

			safetyMetricsPlanetOsmLine.setNumberOfIncidents(
					safetyMetricsPlanetOsmLine.getNumberOfIncidents() + 1);
			if (incident.isScary()) {
				safetyMetricsPlanetOsmLine.setNumberOfScaryIncidents(
						safetyMetricsPlanetOsmLine.getNumberOfScaryIncidents() + 1);
			}

			switch (incident.getIncidentType()) {
				case IncidentType.PULLING_IN_OUT ->
					safetyMetricsPlanetOsmLine.setNumberOfPullInOuts(
							safetyMetricsPlanetOsmLine.getNumberOfPullInOuts() + 1);
				case IncidentType.CLOSE_PASS ->
					safetyMetricsPlanetOsmLine.setNumberOfClosePasses(
							safetyMetricsPlanetOsmLine.getNumberOfClosePasses() + 1);
				case IncidentType.NEAR_LEFT_RIGHT_HOOK ->
					safetyMetricsPlanetOsmLine.setNumberOfNearLeftRightHooks(
							safetyMetricsPlanetOsmLine.getNumberOfNearLeftRightHooks() + 1);
				case IncidentType.HEAD_ON_APPROACH ->
					safetyMetricsPlanetOsmLine.setNumberOfHeadOnApproaches(
							safetyMetricsPlanetOsmLine.getNumberOfHeadOnApproaches() + 1);
				case IncidentType.TAILGATING ->
					safetyMetricsPlanetOsmLine.setNumberOfTailgating(
							safetyMetricsPlanetOsmLine.getNumberOfTailgating() + 1);
				case IncidentType.NEAR_DOORING ->
					safetyMetricsPlanetOsmLine.setNumberOfNearDoorings(
							safetyMetricsPlanetOsmLine.getNumberOfNearDoorings() + 1);
				case IncidentType.DODGING_OBSTACLE ->
					safetyMetricsPlanetOsmLine.setNumberOfObstacleDodges(
							safetyMetricsPlanetOsmLine.getNumberOfObstacleDodges() + 1);
			}

			trafficTimesSafetyMetricsHashBiMap.put(key,
					safetyMetricsPlanetOsmLine);
		}

		calculateAllWeekValues(trafficTimesSafetyMetricsHashBiMap);
		calculateAllDayValues(trafficTimesSafetyMetricsHashBiMap);
		calculateAllDayAllWeekValue(trafficTimesSafetyMetricsHashBiMap);

		trafficTimesSafetyMetricsHashBiMap.forEach((trafficTimeWeekDayKey, safetyMetrics) -> {
			safetyMetrics.setTrafficTime(trafficTimeWeekDayKey.getTrafficTime());
			safetyMetrics.setWeekDay(trafficTimeWeekDayKey.getWeekDay());

			float dangerousScore = calculateDangerousScore(safetyMetrics);
			safetyMetrics.setDangerousScore(dangerousScore);
			String dangerousColor = DangerousScoreToColorMap.getColorForScore(dangerousScore);
			safetyMetrics.setDangerousColor(dangerousColor);
		});

		return trafficTimesSafetyMetricsHashBiMap;
	}

	private float calculateDangerousScore(SafetyMetrics safetyMetrics) {
		int numberOfNonScaryIncidents = safetyMetrics.getNumberOfIncidents()
				- safetyMetrics.getNumberOfScaryIncidents();

		return ((SCARINESS_FACTOR * safetyMetrics.getNumberOfScaryIncidents() + numberOfNonScaryIncidents)
				/ safetyMetrics.getNumberOfRides());
	}


	void calculateAllDayValues(HashBiMap<TrafficTimeWeekDayKey, SafetyMetricsPlanetOsmLine> metricsMap) {
		for (WeekDays wd : List.of(WeekDays.WEEK, WeekDays.WEEKEND)) {
			SafetyMetricsPlanetOsmLine aggregatedDayMetric = new SafetyMetricsPlanetOsmLine();
			for (TrafficTimes tt : List.of(TrafficTimes.MORNING_RUSH_HOUR, TrafficTimes.MID_DAY, TrafficTimes.EVENING_RUSH_HOUR, TrafficTimes.EVENING_NIGHT_MORNING)) {
				TrafficTimeWeekDayKey key = new TrafficTimeWeekDayKey(tt, wd);
				if (metricsMap.containsKey(key)) {
					aggregatedDayMetric.addUpSafetyMetric(metricsMap.get(key));
				}
			}
			TrafficTimeWeekDayKey allWeekKey = new TrafficTimeWeekDayKey(TrafficTimes.ALL_DAY, wd);
			metricsMap.put(allWeekKey, aggregatedDayMetric);
		}
	}

	void calculateAllWeekValues(HashBiMap<TrafficTimeWeekDayKey, SafetyMetricsPlanetOsmLine> metricsMap) {
		for (TrafficTimes tt : List.of(TrafficTimes.MORNING_RUSH_HOUR, TrafficTimes.MID_DAY, TrafficTimes.EVENING_RUSH_HOUR, TrafficTimes.EVENING_NIGHT_MORNING)) {
			SafetyMetricsPlanetOsmLine aggregatedDayMetric = new SafetyMetricsPlanetOsmLine();
			for (WeekDays wd : List.of(WeekDays.WEEK, WeekDays.WEEKEND)) {
				TrafficTimeWeekDayKey key = new TrafficTimeWeekDayKey(tt, wd);

				if (metricsMap.containsKey(key)) {
					aggregatedDayMetric.addUpSafetyMetric(metricsMap.get(key));
				}
			}
			TrafficTimeWeekDayKey allWeekKey = new TrafficTimeWeekDayKey(tt, WeekDays.ALL_WEEK);
			metricsMap.put(allWeekKey, aggregatedDayMetric);
		}
	}

	void calculateAllDayAllWeekValue(HashBiMap<TrafficTimeWeekDayKey, SafetyMetricsPlanetOsmLine> metricsMap) {
		SafetyMetricsPlanetOsmLine aggregatedDayMetric = new SafetyMetricsPlanetOsmLine();

		for (WeekDays wd : List.of(WeekDays.WEEK, WeekDays.WEEKEND)) {
			TrafficTimeWeekDayKey key = new TrafficTimeWeekDayKey(TrafficTimes.ALL_DAY, wd);

			if (metricsMap.containsKey(key)) {
				aggregatedDayMetric.addUpSafetyMetric(metricsMap.get(key));
			}
		}

		TrafficTimeWeekDayKey allDayAllWeekKey = new TrafficTimeWeekDayKey(TrafficTimes.ALL_DAY, WeekDays.ALL_WEEK);
		metricsMap.put(allDayAllWeekKey, aggregatedDayMetric);
	}
}
