package com.simra.konsumgandalf.osmPlanet.services;

import com.simra.konsumgandalf.common.logging.LogExecutionTime;
import com.simra.konsumgandalf.common.models.entities.Region;
import com.simra.konsumgandalf.common.models.entities.SafetyMetricsRegion;
import com.simra.konsumgandalf.common.models.entities.SafetyMetricsSimraRegion;
import com.simra.konsumgandalf.common.models.entities.SimraRegion;
import com.simra.konsumgandalf.common.models.enums.TrafficTimes;
import com.simra.konsumgandalf.common.models.enums.WeekDays;
import com.simra.konsumgandalf.common.models.maps.DangerousScoreToColorMap;
import com.simra.konsumgandalf.osmPlanet.classes.dtos.RideEntityTotalDTO;
import com.simra.konsumgandalf.osmPlanet.classes.dtos.TimeFilters;
import com.simra.konsumgandalf.osmPlanet.classes.keys.RegionTrafficTimeWeekDayKey;
import com.simra.konsumgandalf.osmPlanet.classes.mapper.SimraRegionMapper;
import com.simra.konsumgandalf.osmPlanet.repositories.OsmPolygonRepository;
import com.simra.konsumgandalf.osmPlanet.repositories.RegionRepository;
import com.simra.konsumgandalf.osmPlanet.repositories.SafetyMetricsSimraRegionRepository;
import com.simra.konsumgandalf.osmPlanet.repositories.SimraRegionRepository;
import jakarta.transaction.Transactional;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKBReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.simra.konsumgandalf.osmPlanet.utils.ScoreUtils.calculateDangerousScore;
import static com.simra.konsumgandalf.osmPlanet.utils.TimeFilterUtils.getTimeFilters;

/**
 * This service provides analytics for the OSM planet.
 */
@Transactional
@Service
public class AnalyticsServiceSimraRegionMetrics {

	@Autowired
	private SafetyMetricsSimraRegionRepository safetyMetricsSimraRegionRepository;

	@Autowired
	private RegionRepository regionRepository;

	@Autowired
	private SimraRegionRepository simraRegionRepository;

	private static final Logger _logger = LoggerFactory.getLogger(AnalyticsServiceSimraRegionMetrics.class);

	private static final SimraRegionMapper simraMapper = new SimraRegionMapper();

	private static final WKBReader geometryReader = new WKBReader();

	@Async
	@LogExecutionTime
	public void calculateSafetyMetricsSimraRegion() {
		List<SimraRegion> simraRegions = createOrUpdateSimraRegions();
		Map<RegionTrafficTimeWeekDayKey, SafetyMetricsRegion> metricsRegionHashMap = accumulateSafetyMetrics(
				simraRegions);
		convertAndSaveSimraRegionMetrics(metricsRegionHashMap);
	}

	private List<SimraRegion> createOrUpdateSimraRegions() {
		Map<String, List<String>> simraRegionMap = new HashMap<>(simraMapper.map);
		simraRegionMap.put("All",
				regionRepository.findAll()
					.stream()
					.filter(region -> region.getAdminLevel() == 4)
					.map(Region::getName)
					.toList());
		List<SimraRegion> simraRegions = new ArrayList<>();
		for (Map.Entry<String, List<String>> entry : simraRegionMap.entrySet()) {
			String simraRegionName = entry.getKey();
			SimraRegion simraRegion = simraRegionRepository.findByName(simraRegionName)
				.orElseGet(() -> new SimraRegion(simraRegionName));
			List<Region> regions = new ArrayList<>();
			for (String regionString : entry.getValue()) {
				Optional<Region> optionalRegion = regionRepository.findByName(regionString);
				if (optionalRegion.isEmpty()) {
					_logger.warn("State {} not found in database", regionString);
					continue;
				}
				Region region = optionalRegion.get();
				region.setSimraRegions(new ArrayList<>(List.of(simraRegion)));
				regions.add(region);
			}
			if (regions.isEmpty()) {
				_logger.warn("No regions found for simra region {}", simraRegionName);
				continue;
			}

			updateSimraRegionGeometry(simraRegion, regions);
			simraRegion.setRegions(regions);
			simraRegions.add(simraRegion);
		}
		simraRegionRepository.saveAll(simraRegions);
		regionRepository.flush();
		return simraRegions;
	}

	private Map<RegionTrafficTimeWeekDayKey, SafetyMetricsRegion> accumulateSafetyMetrics(
			List<SimraRegion> simraRegions) {
		Map<RegionTrafficTimeWeekDayKey, SafetyMetricsRegion> metricsRegionHashMap = new HashMap<>();
		for (SimraRegion simraRegion : simraRegions) {
			for (Region region : simraRegion.getRegions()) {
				for (SafetyMetricsRegion safetyMetricsRegion : region.getSafetyMetricsRegions()) {
					RegionTrafficTimeWeekDayKey key = new RegionTrafficTimeWeekDayKey(simraRegion,
							safetyMetricsRegion.getTrafficTime(), safetyMetricsRegion.getWeekDay(),
							safetyMetricsRegion.getYear());
					metricsRegionHashMap.merge(key, safetyMetricsRegion, SafetyMetricsRegion::addUpSafetyMetric);
				}
			}
		}
		return metricsRegionHashMap;
	}

	private void convertAndSaveSimraRegionMetrics(
			Map<RegionTrafficTimeWeekDayKey, SafetyMetricsRegion> metricsRegionHashMap) {
		List<SafetyMetricsSimraRegion> safetyMetricsRegionList = new ArrayList<>();
		for (Map.Entry<RegionTrafficTimeWeekDayKey, SafetyMetricsRegion> entry : metricsRegionHashMap.entrySet()) {
			RegionTrafficTimeWeekDayKey key = entry.getKey();
			SafetyMetricsRegion safetyMetricsRegion = entry.getValue();
			RideEntityTotalDTO totalRides = this.totalRideMetersPerSimraRegion(key.getSimraRegion().getName(),
					safetyMetricsRegion.getTrafficTime(), safetyMetricsRegion.getWeekDay(),
					safetyMetricsRegion.getYear());
			SafetyMetricsSimraRegion safetyMetricsSimraRegion = new SafetyMetricsSimraRegion(
					totalRides.getTotalDistance(), safetyMetricsRegion.getTrafficTime(),
					safetyMetricsRegion.getWeekDay(), safetyMetricsRegion.getYear(),
					Math.toIntExact(totalRides.getTotalRides()), safetyMetricsRegion.getNumberOfIncidents(),
					safetyMetricsRegion.getNumberOfScaryIncidents(), safetyMetricsRegion.getNumberOfClosePasses(),
					safetyMetricsRegion.getNumberOfPullInOuts(), safetyMetricsRegion.getNumberOfNearLeftRightHooks(),
					safetyMetricsRegion.getNumberOfHeadOnApproaches(), safetyMetricsRegion.getNumberOfTailgating(),
					safetyMetricsRegion.getNumberOfNearDoorings(), safetyMetricsRegion.getNumberOfObstacleDodges());
			safetyMetricsSimraRegion.setRegion(key.getSimraRegion());
			safetyMetricsSimraRegion.setName(key.getSimraRegion().getName());
			float dangerousScore = calculateDangerousScore(
					Math.round(totalRides.getTotalDistance() / key.getSimraRegion().getAvgSegmentDistance()),
					safetyMetricsSimraRegion.getNumberOfIncidents(),
					safetyMetricsSimraRegion.getNumberOfScaryIncidents());
			safetyMetricsSimraRegion.setDangerousScore(dangerousScore);
			safetyMetricsSimraRegion.setDangerousColor(DangerousScoreToColorMap.getColorForScore(dangerousScore));
			safetyMetricsRegionList.add(safetyMetricsSimraRegion);
		}
		safetyMetricsSimraRegionRepository.saveAll(safetyMetricsRegionList);
	}

	private RideEntityTotalDTO totalRideMetersPerSimraRegion(String name, TrafficTimes time, WeekDays weekDay,
			Integer year) {
		TimeFilters timeFilters = getTimeFilters(time, weekDay, year);

		return simraRegionRepository.totalRides(name, timeFilters.trafficTimes(), timeFilters.weekDays(),
				timeFilters.years());
	}

	private Map<String, List<String>> prepareSimraRegionMap() {
		Map<String, List<String>> simraRegionMap = new HashMap<>(simraMapper.map);

		// Just use Level 4 since all level 6 regions are already included in level 4
		simraRegionMap.put("All",
				regionRepository.findAll()
					.stream()
					.filter(region -> region.getAdminLevel() == 4)
					.map(Region::getName)
					.toList());

		return simraRegionMap;
	}

	private void updateSimraRegionGeometry(SimraRegion simraRegion, List<Region> regions) {
		if (simraRegion.getWay() == null || simraRegion.getAvgSegmentDistance() == null) {
			try {
				byte[] unifiedBytes = regionRepository.unifyRegionWays(regions.stream().map(Region::getName).toList());
				Geometry way = geometryReader.read(unifiedBytes);
				way.setSRID(3857);
				simraRegion.setWay(way);

				Float avgDistance = simraRegionRepository.getAvgSegmentDistance(way);
				simraRegion.setAvgSegmentDistance(avgDistance);
			}
			catch (ParseException e) {
				_logger.error("Error while unifying way for SimraRegion {}", simraRegion.getName(), e);
			}
		}
	}

}
