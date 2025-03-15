package com.simra.konsumgandalf.osmPlanet.services;

import com.google.common.collect.HashBiMap;
import com.simra.konsumgandalf.common.logging.LogExecutionTime;
import com.simra.konsumgandalf.common.models.entities.PlanetOsmLine;
import com.simra.konsumgandalf.common.models.entities.Region;
import com.simra.konsumgandalf.common.models.entities.RideIncident;
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
import com.simra.konsumgandalf.osmPlanet.classes.dtos.RideEntityTotalDTO;
import com.simra.konsumgandalf.osmPlanet.classes.dtos.TimeFilters;
import com.simra.konsumgandalf.osmPlanet.classes.keys.RegionTrafficTimeWeekDayKey;
import com.simra.konsumgandalf.osmPlanet.classes.keys.TrafficTimeWeekDayKey;
import com.simra.konsumgandalf.osmPlanet.classes.mapper.SimraRegionMapper;
import com.simra.konsumgandalf.osmPlanet.repositories.OsmHighwayRepository;
import com.simra.konsumgandalf.osmPlanet.repositories.OsmPolygonRepository;
import com.simra.konsumgandalf.osmPlanet.repositories.RegionRepository;
import com.simra.konsumgandalf.osmPlanet.repositories.SafetyMetricsPlanetOsmLineRepository;
import com.simra.konsumgandalf.osmPlanet.repositories.SafetyMetricsRegionRepository;
import com.simra.konsumgandalf.osmPlanet.repositories.SafetyMetricsSimraRegionRepository;
import com.simra.konsumgandalf.osmPlanet.repositories.SimraRegionRepository;
import jakarta.transaction.Transactional;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKBReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

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

	@Autowired
	private OsmPolygonRepository osmPolygonRepository;

	private final Calendar calendar = new GregorianCalendar();

	private final float SCARINESS_FACTOR = 4.4f;

	private final int PAGE_SIZE = 100;

	private static final Logger _logger = LoggerFactory.getLogger(AnalyticsService.class);

	private static final SimraRegionMapper simraMapper = new SimraRegionMapper();

	private static final WKBReader geometryReader = new WKBReader();

	// @Scheduled(cron = CronExpressions.EVERY_DAY)
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
		List<RegionSafetyMetricsProjection> statesSafetyMetrics = safetyMetricsLineRepository
			.getRegionSafetyMetricsOfAdminLevel(List.of(4, 6));

		List<Region> regions = statesSafetyMetrics.stream().filter(safetyMetricsProjection -> {
			Optional<Region> region = regionRepository.findByName(safetyMetricsProjection.getName());
			return region.isEmpty();
		}).map(safetyMetricsProjection -> {
			return new Region(safetyMetricsProjection.getName(), safetyMetricsProjection.getOsmId(),
					Math.toIntExact(safetyMetricsProjection.getAdminLevel()));
		}).distinct().map(region -> {
			Float avgDistance = osmPolygonRepository.getAvgSegmentDistance(region.getId());
			region.setAvgSegmentDistance(avgDistance);

			Geometry way = osmPolygonRepository.getWayByOsmId(region.getId());
			region.setWay(way);

			return region;
		}).toList();
		regionRepository.saveAll(regions);

		ArrayList<SafetyMetricsRegion> safetyMetricsRegionList = new ArrayList<>();
		for (RegionSafetyMetricsProjection safetyMetricsProjection : statesSafetyMetrics) {
			Region region = regionRepository.findByName(safetyMetricsProjection.getName()).orElseThrow();

			RideEntityTotalDTO totalRides = this.totalRideMetersPerRegion(region.getId(),
					safetyMetricsProjection.getTrafficTime(), safetyMetricsProjection.getWeekDay(),
					safetyMetricsProjection.getYear());

			if (safetyMetricsProjection.getTrafficTime() == TrafficTimes.EVENING_RUSH_HOUR
					&& safetyMetricsProjection.getWeekDay() == WeekDays.WEEK
					&& safetyMetricsProjection.getYear() == 2024 && Objects.equals(region.getName(), "Berlin")) {
				_logger.info("Total rides for region {}: {}", region.getName(), totalRides.getTotalRides());
			}

			SafetyMetricsRegion safetyMetrics = new SafetyMetricsRegion(totalRides.getTotalDistance(),
					safetyMetricsProjection.getTrafficTime(), safetyMetricsProjection.getWeekDay(),
					safetyMetricsProjection.getYear(), Math.toIntExact(totalRides.getTotalRides()),
					Math.toIntExact(safetyMetricsProjection.getTotalIncidents()),
					Math.toIntExact(safetyMetricsProjection.getTotalScaryIncidents()),
					Math.toIntExact(safetyMetricsProjection.getTotalClosePasses()),
					Math.toIntExact(safetyMetricsProjection.getTotalPullInOuts()),
					Math.toIntExact(safetyMetricsProjection.getTotalNearLeftRightHooks()),
					Math.toIntExact(safetyMetricsProjection.getTotalHeadOnApproaches()),
					Math.toIntExact(safetyMetricsProjection.getTotalTailgating()),
					Math.toIntExact(safetyMetricsProjection.getTotalNearDoorings()),
					Math.toIntExact(safetyMetricsProjection.getTotalObstacleDodges()));

			float dangerousScore = calculateDangerousScore(
					Math.round(totalRides.getTotalDistance() / region.getAvgSegmentDistance()),
					safetyMetrics.getNumberOfIncidents(), safetyMetrics.getNumberOfScaryIncidents());
			safetyMetrics.setDangerousScore(dangerousScore);

			String dangerousColor = DangerousScoreToColorMap.getColorForScore(dangerousScore);
			safetyMetrics.setDangerousColor(dangerousColor);

			safetyMetrics.setRegion(region);
			safetyMetrics.setName(region.getName());

			safetyMetricsRegionList.add(safetyMetrics);
		}
		safetyMetricsRegionRepository.saveAll(safetyMetricsRegionList);
	}

	private RideEntityTotalDTO totalRideMetersPerRegion(Long regionId, TrafficTimes time, WeekDays weekDay,
			Integer year) {
		TimeFilters timeFilters = getTimeFilters(time, weekDay, year);

		return osmPolygonRepository.totalRidesByRegion(regionId, timeFilters.trafficTimes(), timeFilters.weekDays(),
				timeFilters.years());
	}

	private RideEntityTotalDTO totalRideMetersPerSimraRegion(String name, TrafficTimes time, WeekDays weekDay,
			Integer year) {
		TimeFilters timeFilters = getTimeFilters(time, weekDay, year);

		return simraRegionRepository.totalRides(name, timeFilters.trafficTimes(), timeFilters.weekDays(),
				timeFilters.years());
	}

	public void calculateSafetyMetricsSimraRegion() {
		ArrayList<SimraRegion> simraRegions = new ArrayList<>();
		ArrayList<SafetyMetricsSimraRegion> safetyMetricsRegionList = new ArrayList<>();
		HashMap<RegionTrafficTimeWeekDayKey, SafetyMetricsRegion> metricsRegionHashMap = new HashMap();

		Map<String, List<String>> simraRegionMap = simraMapper.map;
		simraRegionMap.put("All",
				regionRepository.findAll()
					.stream()
					.filter(region -> region.getAdminLevel() == 4)
					.map(Region::getName)
					.toList());
		for (Map.Entry<String, List<String>> entry : simraRegionMap.entrySet()) {
			String simraRegionName = entry.getKey();
			SimraRegion simraRegion = simraRegionRepository.findByName(simraRegionName)
				.orElseGet(() -> new SimraRegion(simraRegionName));

			ArrayList<Region> regions = new ArrayList<>();
			for (String regionString : entry.getValue()) {
				Optional<Region> optionalRegion = regionRepository.findByName(regionString);

				if (optionalRegion.isEmpty()) {
					_logger.warn("State {} not found in database", regionString);
					continue;
				}

				Region region = optionalRegion.get();
				region.setSimraRegions(List.of(simraRegion));
				regions.add(region);
				for (SafetyMetricsRegion safetyMetricsRegion : region.getSafetyMetricsRegions()) {
					RegionTrafficTimeWeekDayKey key = new RegionTrafficTimeWeekDayKey(simraRegion,
							safetyMetricsRegion.getTrafficTime(), safetyMetricsRegion.getWeekDay(),
							safetyMetricsRegion.getYear());
					if (metricsRegionHashMap.containsKey(key)) {
						SafetyMetricsRegion existingSafetyMetricsRegion = metricsRegionHashMap.get(key);
						SafetyMetricsRegion combinedMetrics = existingSafetyMetricsRegion
							.addUpSafetyMetric(safetyMetricsRegion);
						metricsRegionHashMap.put(key, combinedMetrics);
					}
					else {
						metricsRegionHashMap.put(key, safetyMetricsRegion);
					}
				}
			}

			if (regions.isEmpty()) {
				_logger.warn("No regions found for simra region {}", entry.getKey());
				continue;
			}

			if (simraRegion.getWay() == null || simraRegion.getAvgSegmentDistance() == null) {
				try {
					byte[] unifiedBytes = regionRepository
						.unifyRegionWays(regions.stream().map(Region::getName).toList());
					Geometry way = geometryReader.read(unifiedBytes);
					way.setSRID(3857);
					simraRegion.setWay(way);

					Float avgDistance = simraRegionRepository.getAvgSegmentDistance(way);
					simraRegion.setAvgSegmentDistance(avgDistance);
				}
				catch (ParseException e) {
					_logger.error("Error while unifying way for simra region {}", entry.getKey());
				}
			}

			simraRegion.setRegions(regions);
			simraRegions.add(simraRegion);
		}

		simraRegionRepository.saveAll(simraRegions);
		regionRepository.flush();

		for (Map.Entry<RegionTrafficTimeWeekDayKey, SafetyMetricsRegion> entry : metricsRegionHashMap.entrySet()) {
			SafetyMetricsRegion safetyMetricsRegion = entry.getValue();

			RideEntityTotalDTO totalRides = this.totalRideMetersPerSimraRegion(
					entry.getKey().getSimraRegion().getName(), safetyMetricsRegion.getTrafficTime(),
					safetyMetricsRegion.getWeekDay(), safetyMetricsRegion.getYear());

			SafetyMetricsSimraRegion safetyMetricsSimraRegion = new SafetyMetricsSimraRegion(
					totalRides.getTotalDistance(), safetyMetricsRegion.getTrafficTime(),
					safetyMetricsRegion.getWeekDay(), safetyMetricsRegion.getYear(),
					Math.toIntExact(totalRides.getTotalRides()), safetyMetricsRegion.getNumberOfIncidents(),
					safetyMetricsRegion.getNumberOfScaryIncidents(), safetyMetricsRegion.getNumberOfClosePasses(),
					safetyMetricsRegion.getNumberOfPullInOuts(), safetyMetricsRegion.getNumberOfNearLeftRightHooks(),
					safetyMetricsRegion.getNumberOfHeadOnApproaches(), safetyMetricsRegion.getNumberOfTailgating(),
					safetyMetricsRegion.getNumberOfNearDoorings(), safetyMetricsRegion.getNumberOfObstacleDodges());

			safetyMetricsSimraRegion.setRegion(entry.getKey().getSimraRegion());
			safetyMetricsSimraRegion.setName(entry.getKey().getSimraRegion().getName());

			float dangerousScore = calculateDangerousScore(
					Math.round(totalRides.getTotalDistance() / entry.getKey().getSimraRegion().getAvgSegmentDistance()),
					safetyMetricsSimraRegion.getNumberOfIncidents(),
					safetyMetricsSimraRegion.getNumberOfScaryIncidents());
			safetyMetricsSimraRegion.setDangerousScore(dangerousScore);

			String dangerousColor = DangerousScoreToColorMap.getColorForScore(dangerousScore);
			safetyMetricsSimraRegion.setDangerousColor(dangerousColor);

			safetyMetricsRegionList.add(safetyMetricsSimraRegion);
		}

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
				/*
				 * @TODO Anonymize the data later if (safetyMetric.getNumberOfRides() < 5)
				 * { return; }
				 */

				safetyMetric.setPlanetOsmLine(street);
				safetyMetric.setTrafficTime(key.getTrafficTime());
				safetyMetric.setWeekDay(key.getWeekDay());
				safetyMetric.setYear(key.getYear());
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
		HashBiMap<TrafficTimeWeekDayKey, SafetyMetricsPlanetOsmLine> trafficTimesSafetyMetricsHashBiMap = HashBiMap
			.create();

		for (FindNumberOfRidesWithinStreetSegmentInTimePeriodDTO ele : osmHighwayRepository
			.findNumberOfRidesWithinStreetSegmentInTimePeriod(street.getId())) {
			TrafficTimeWeekDayKey key = ele.getTrafficTimeWeekDayKey();

			SafetyMetricsPlanetOsmLine sm = new SafetyMetricsPlanetOsmLine();
			sm.setNumberOfRides(ele.getNumberOfRides());

			trafficTimesSafetyMetricsHashBiMap.put(key, sm);
		}

		for (RideIncident incident : street.getRideIncident()) {
			TrafficTimeWeekDayKey key = new TrafficTimeWeekDayKey(incident.getTrafficTime(), incident.getWeekDay(),
					incident.getYear());
			if (!trafficTimesSafetyMetricsHashBiMap.containsKey(key)) {
				_logger.warn("No safety metrics found for key: {}", key);
				continue;
			}
			SafetyMetricsPlanetOsmLine safetyMetricsPlanetOsmLine = trafficTimesSafetyMetricsHashBiMap.get(key);

			safetyMetricsPlanetOsmLine.setNumberOfIncidents(safetyMetricsPlanetOsmLine.getNumberOfIncidents() + 1);
			if (incident.isScary()) {
				safetyMetricsPlanetOsmLine
					.setNumberOfScaryIncidents(safetyMetricsPlanetOsmLine.getNumberOfScaryIncidents() + 1);
			}

			switch (incident.getIncidentType()) {
				case IncidentType.PULLING_IN_OUT -> safetyMetricsPlanetOsmLine
					.setNumberOfPullInOuts(safetyMetricsPlanetOsmLine.getNumberOfPullInOuts() + 1);
				case IncidentType.CLOSE_PASS -> safetyMetricsPlanetOsmLine
					.setNumberOfClosePasses(safetyMetricsPlanetOsmLine.getNumberOfClosePasses() + 1);
				case IncidentType.NEAR_LEFT_RIGHT_HOOK -> safetyMetricsPlanetOsmLine
					.setNumberOfNearLeftRightHooks(safetyMetricsPlanetOsmLine.getNumberOfNearLeftRightHooks() + 1);
				case IncidentType.HEAD_ON_APPROACH -> safetyMetricsPlanetOsmLine
					.setNumberOfHeadOnApproaches(safetyMetricsPlanetOsmLine.getNumberOfHeadOnApproaches() + 1);
				case IncidentType.TAILGATING -> safetyMetricsPlanetOsmLine
					.setNumberOfTailgating(safetyMetricsPlanetOsmLine.getNumberOfTailgating() + 1);
				case IncidentType.NEAR_DOORING -> safetyMetricsPlanetOsmLine
					.setNumberOfNearDoorings(safetyMetricsPlanetOsmLine.getNumberOfNearDoorings() + 1);
				case IncidentType.DODGING_OBSTACLE -> safetyMetricsPlanetOsmLine
					.setNumberOfObstacleDodges(safetyMetricsPlanetOsmLine.getNumberOfObstacleDodges() + 1);
			}

			trafficTimesSafetyMetricsHashBiMap.put(key, safetyMetricsPlanetOsmLine);
		}

		calculateAllYearValues(trafficTimesSafetyMetricsHashBiMap);

		trafficTimesSafetyMetricsHashBiMap.forEach((trafficTimeWeekDayKey, safetyMetrics) -> {
			safetyMetrics.setTrafficTime(trafficTimeWeekDayKey.getTrafficTime());
			safetyMetrics.setWeekDay(trafficTimeWeekDayKey.getWeekDay());
			safetyMetrics.setYear(trafficTimeWeekDayKey.getYear());

			float dangerousScore = calculateDangerousScore(safetyMetrics.getNumberOfRides(),
					safetyMetrics.getNumberOfIncidents(), safetyMetrics.getNumberOfScaryIncidents());
			safetyMetrics.setDangerousScore(dangerousScore);
			String dangerousColor = DangerousScoreToColorMap.getColorForScore(dangerousScore);
			safetyMetrics.setDangerousColor(dangerousColor);
		});

		return trafficTimesSafetyMetricsHashBiMap;
	}

	private float calculateDangerousScore(int numberOfRides, int numberOfIncidents, int numberOfScaryIncidents) {
		int numberOfNonScaryIncidents = numberOfIncidents - numberOfScaryIncidents;

		return ((SCARINESS_FACTOR * numberOfScaryIncidents + numberOfNonScaryIncidents) / numberOfRides);
	}

	private List<Integer> getAllYears(HashBiMap<TrafficTimeWeekDayKey, SafetyMetricsPlanetOsmLine> metricsMap) {
		return metricsMap.keySet()
			.stream()
			.map(TrafficTimeWeekDayKey::getYear)
			.filter(year -> year != 2000)
			.distinct()
			.toList();
	}

	void calculateAllYearValues(HashBiMap<TrafficTimeWeekDayKey, SafetyMetricsPlanetOsmLine> metricsMap) {
		List<Integer> allYears = getAllYears(metricsMap);
		for (Integer year : allYears) {
			calculateAllWeekValues(metricsMap, year);
			calculateAllDayValues(metricsMap, year);
			calculateAllDayAllWeekValue(metricsMap, year);
		}

		HashBiMap<TrafficTimeWeekDayKey, SafetyMetricsPlanetOsmLine> tempAggregatedMap = HashBiMap.create();

		for (WeekDays wd : WeekDays.values()) {
			for (TrafficTimes tt : TrafficTimes.values()) {
				SafetyMetricsPlanetOsmLine aggregatedYearMetric = new SafetyMetricsPlanetOsmLine();
				boolean hasData = false;

				for (Integer year : allYears) {
					TrafficTimeWeekDayKey key = new TrafficTimeWeekDayKey(tt, wd, year);
					if (metricsMap.containsKey(key)) {
						aggregatedYearMetric.addUpSafetyMetric(metricsMap.get(key));
						hasData = true;
					}
				}

				if (hasData) {
					TrafficTimeWeekDayKey allYearKey = new TrafficTimeWeekDayKey(tt, wd, 2000);
					tempAggregatedMap.put(allYearKey, aggregatedYearMetric);
				}
			}
		}

		metricsMap.putAll(tempAggregatedMap);
	}

	void calculateAllDayValues(HashBiMap<TrafficTimeWeekDayKey, SafetyMetricsPlanetOsmLine> metricsMap, int year) {
		for (WeekDays wd : List.of(WeekDays.WEEK, WeekDays.WEEKEND)) {
			SafetyMetricsPlanetOsmLine aggregatedDayMetric = new SafetyMetricsPlanetOsmLine();
			for (TrafficTimes tt : List.of(TrafficTimes.MORNING_RUSH_HOUR, TrafficTimes.MID_DAY,
					TrafficTimes.EVENING_RUSH_HOUR, TrafficTimes.EVENING_NIGHT_MORNING)) {
				TrafficTimeWeekDayKey key = new TrafficTimeWeekDayKey(tt, wd, year);
				if (metricsMap.containsKey(key)) {
					aggregatedDayMetric.addUpSafetyMetric(metricsMap.get(key));
				}
			}
			TrafficTimeWeekDayKey allWeekKey = new TrafficTimeWeekDayKey(TrafficTimes.ALL_DAY, wd, year);

			if (aggregatedDayMetric.getNumberOfRides() == 0) {
				continue;
			}
			metricsMap.put(allWeekKey, aggregatedDayMetric);
		}
	}

	void calculateAllWeekValues(HashBiMap<TrafficTimeWeekDayKey, SafetyMetricsPlanetOsmLine> metricsMap, int year) {
		for (TrafficTimes tt : List.of(TrafficTimes.MORNING_RUSH_HOUR, TrafficTimes.MID_DAY,
				TrafficTimes.EVENING_RUSH_HOUR, TrafficTimes.EVENING_NIGHT_MORNING)) {
			SafetyMetricsPlanetOsmLine aggregatedDayMetric = new SafetyMetricsPlanetOsmLine();
			for (WeekDays wd : List.of(WeekDays.WEEK, WeekDays.WEEKEND)) {
				TrafficTimeWeekDayKey key = new TrafficTimeWeekDayKey(tt, wd, year);

				if (metricsMap.containsKey(key)) {
					aggregatedDayMetric.addUpSafetyMetric(metricsMap.get(key));
				}
			}
			TrafficTimeWeekDayKey allWeekKey = new TrafficTimeWeekDayKey(tt, WeekDays.ALL_WEEK, year);

			if (aggregatedDayMetric.getNumberOfRides() == 0) {
				continue;
			}
			metricsMap.put(allWeekKey, aggregatedDayMetric);
		}
	}

	void calculateAllDayAllWeekValue(HashBiMap<TrafficTimeWeekDayKey, SafetyMetricsPlanetOsmLine> metricsMap,
			int year) {
		SafetyMetricsPlanetOsmLine aggregatedDayMetric = new SafetyMetricsPlanetOsmLine();

		for (WeekDays wd : List.of(WeekDays.WEEK, WeekDays.WEEKEND)) {
			TrafficTimeWeekDayKey key = new TrafficTimeWeekDayKey(TrafficTimes.ALL_DAY, wd, year);

			if (metricsMap.containsKey(key)) {
				aggregatedDayMetric.addUpSafetyMetric(metricsMap.get(key));
			}
		}

		TrafficTimeWeekDayKey allDayAllWeekKey = new TrafficTimeWeekDayKey(TrafficTimes.ALL_DAY, WeekDays.ALL_WEEK,
				year);
		metricsMap.put(allDayAllWeekKey, aggregatedDayMetric);
	}

	private TimeFilters getTimeFilters(TrafficTimes time, WeekDays weekDay, Integer year) {
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

}
