package com.simra.konsumgandalf.osmPlanet.services;

import com.simra.konsumgandalf.common.logging.LogExecutionTime;
import com.simra.konsumgandalf.common.models.entities.Region;
import com.simra.konsumgandalf.common.models.entities.SafetyMetricsRegion;
import com.simra.konsumgandalf.common.models.entities.SafetyMetricsSimraRegion;
import com.simra.konsumgandalf.common.models.entities.SimraRegion;
import com.simra.konsumgandalf.common.models.maps.DangerousScoreToColorMap;
import com.simra.konsumgandalf.osmPlanet.classes.dtos.RideEntityMetricsDTO;
import com.simra.konsumgandalf.osmPlanet.classes.dtos.RideEntityTotalDTO;
import com.simra.konsumgandalf.osmPlanet.classes.keys.RegionTrafficTimeWeekDayKey;
import com.simra.konsumgandalf.osmPlanet.classes.mapper.SimraRegionMapper;
import com.simra.konsumgandalf.osmPlanet.repositories.RegionRepository;
import com.simra.konsumgandalf.osmPlanet.repositories.SafetyMetricsSimraRegionRepository;
import com.simra.konsumgandalf.osmPlanet.repositories.SimraRegionRepository;
import com.simra.konsumgandalf.osmPlanet.utils.AnalyticsUtils;
import jakarta.transaction.Transactional;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKBReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import static com.simra.konsumgandalf.common.utils.ScoreUtils.calculateDangerousScore;

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

	@LogExecutionTime
	public void calculateSafetyMetricsSimraRegion() {
		List<SimraRegion> simraRegions = createOrUpdateSimraRegions();
		Map<RegionTrafficTimeWeekDayKey, SafetyMetricsRegion> metricsRegionHashMap = accumulateSafetyMetrics(
				simraRegions);
		_logger.info("Finished calculating safety metrics for simra regions");
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

		List<RideEntityMetricsDTO> totalRidesAndLengthNotAll = safetyMetricsSimraRegionRepository
			.findNumberOfRidesAndLengthNotAll();
		List<RideEntityMetricsDTO> totalRidesAndLengthAll = safetyMetricsSimraRegionRepository
			.findNumberOfRidesAndLengthAll();
		List<RideEntityMetricsDTO> totalRidesAndLength = Stream
			.concat(totalRidesAndLengthNotAll.stream(), totalRidesAndLengthAll.stream())
			.toList();
		_logger.info("Total rides and length per region calculated.");

		for (Map.Entry<RegionTrafficTimeWeekDayKey, SafetyMetricsRegion> entry : metricsRegionHashMap.entrySet()) {
			RegionTrafficTimeWeekDayKey key = entry.getKey();
			SafetyMetricsRegion safetyMetricsRegion = entry.getValue();

			RideEntityTotalDTO totalRides = AnalyticsUtils.totalRideMetersPerRegion(totalRidesAndLength,
					key.getSimraRegion().getName(), safetyMetricsRegion.getTrafficTime(),
					safetyMetricsRegion.getWeekDay(), safetyMetricsRegion.getYear());

			SafetyMetricsSimraRegion safetyMetricsSimraRegion = new SafetyMetricsSimraRegion(totalRides.totalDistance(),
					safetyMetricsRegion.getTrafficTime(), safetyMetricsRegion.getWeekDay(),
					safetyMetricsRegion.getYear(), Math.toIntExact(totalRides.totalRides()),
					safetyMetricsRegion.getNumberOfIncidents(), safetyMetricsRegion.getNumberOfScaryIncidents(),
					safetyMetricsRegion.getNumberOfClosePasses(), safetyMetricsRegion.getNumberOfPullInOuts(),
					safetyMetricsRegion.getNumberOfNearLeftRightHooks(),
					safetyMetricsRegion.getNumberOfHeadOnApproaches(), safetyMetricsRegion.getNumberOfTailgating(),
					safetyMetricsRegion.getNumberOfNearDoorings(), safetyMetricsRegion.getNumberOfObstacleDodges());
			safetyMetricsSimraRegion.setRegion(key.getSimraRegion());
			safetyMetricsSimraRegion.setName(key.getSimraRegion().getName());

			float dangerousScore = calculateDangerousScore(Math.round(totalRides.totalDistance() / 1000),
					safetyMetricsSimraRegion.getNumberOfIncidents(),
					safetyMetricsSimraRegion.getNumberOfScaryIncidents());
			safetyMetricsSimraRegion.setDangerousScore(dangerousScore);
			safetyMetricsSimraRegion.setDangerousColor(DangerousScoreToColorMap.getColorForScore(dangerousScore));
			safetyMetricsRegionList.add(safetyMetricsSimraRegion);
		}
		safetyMetricsSimraRegionRepository.saveAll(safetyMetricsRegionList);
	}

	private void updateSimraRegionGeometry(SimraRegion simraRegion, List<Region> regions) {
		try {
			byte[] unifiedBytes = regionRepository.unifyRegionWays(regions.stream().map(Region::getName).toList());
			Geometry way = geometryReader.read(unifiedBytes);
			way.setSRID(4326);
			simraRegion.setWay(way);
		}
		catch (ParseException e) {
			_logger.error("Error while unifying way for SimraRegion {}", simraRegion.getName(), e);
		}
	}

}
