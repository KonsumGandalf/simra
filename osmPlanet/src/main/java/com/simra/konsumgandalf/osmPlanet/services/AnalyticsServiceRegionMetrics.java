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
import com.simra.konsumgandalf.osmPlanet.utils.TimeFilterUtils;
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

import static com.simra.konsumgandalf.osmPlanet.utils.TimeFilterUtils.getTimeFilters;
import static com.simra.konsumgandalf.osmPlanet.utils.ScoreUtils.calculateDangerousScore;

/**
 * This service provides analytics for the OSM planet.
 */
@Transactional
@Service
public class AnalyticsServiceRegionMetrics {

	@Autowired
	private SafetyMetricsPlanetOsmLineRepository safetyMetricsLineRepository;

	@Autowired
	private SafetyMetricsRegionRepository safetyMetricsRegionRepository;

	@Autowired
	private SafetyMetricsSimraRegionRepository safetyMetricsSimraRegionRepository;

	@Autowired
	private RegionRepository regionRepository;

	@Autowired
	private OsmPolygonRepository osmPolygonRepository;

	private static final Logger _logger = LoggerFactory.getLogger(AnalyticsServiceRegionMetrics.class);

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

}
