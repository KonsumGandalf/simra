package com.simra.konsumgandalf.osmPlanet.services;

import com.google.common.collect.HashBiMap;
import com.simra.konsumgandalf.common.models.entities.PlanetOsmLine;
import com.simra.konsumgandalf.common.models.entities.RideIncident;
import com.simra.konsumgandalf.common.models.entities.SafetyMetricsPlanetOsmLine;
import com.simra.konsumgandalf.common.models.enums.IncidentType;
import com.simra.konsumgandalf.common.models.enums.TrafficTimes;
import com.simra.konsumgandalf.common.models.enums.WeekDays;
import com.simra.konsumgandalf.osmPlanet.classes.dtos.FindNumberOfRidesWithinStreetSegmentInTimePeriodDTO;
import com.simra.konsumgandalf.osmPlanet.classes.keys.TrafficTimeWeekDayKey;
import com.simra.konsumgandalf.osmPlanet.repositories.OsmHighwayRepository;
import com.simra.konsumgandalf.osmPlanet.repositories.SafetyMetricsPlanetOsmLineRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AnalyticsServiceTest {

	@Mock
	private OsmHighwayRepository osmHighwayRepository;

	@Mock
	private SafetyMetricsPlanetOsmLineRepository safetyMetricsRepository;

	@InjectMocks
	private AnalyticsService analyticsService;

	AnalyticsService analyticsServiceSpy;

	@BeforeEach
	public void setUp() {
		analyticsServiceSpy = spy(analyticsService);
	}

	@Test
	public void testCalculateSafetyMetrics() {
		PlanetOsmLine mockLine = new PlanetOsmLine();

		FindNumberOfRidesWithinStreetSegmentInTimePeriodDTO morningRushHourRideDTO = new FindNumberOfRidesWithinStreetSegmentInTimePeriodDTO(
				TrafficTimes.MORNING_RUSH_HOUR, WeekDays.WEEKEND,2000, 10);
		FindNumberOfRidesWithinStreetSegmentInTimePeriodDTO eveningNightRideDTO = new FindNumberOfRidesWithinStreetSegmentInTimePeriodDTO(
				TrafficTimes.EVENING_NIGHT_MORNING, WeekDays.WEEK,2000, 5);
		when(osmHighwayRepository.findNumberOfRidesWithinStreetSegmentInTimePeriod(any(Long.class)))
			.thenReturn(List.of(morningRushHourRideDTO, eveningNightRideDTO));

		RideIncident eveningIncident = new RideIncident();
		eveningIncident.setScary(true);
		eveningIncident.setTrafficTime(TrafficTimes.EVENING_NIGHT_MORNING);
		eveningIncident.setWeekDay(WeekDays.WEEK);
		eveningIncident.setYear(2000);
		eveningIncident.setIncidentType(IncidentType.PULLING_IN_OUT);

		RideIncident eveningFalseIncident = new RideIncident();
		eveningFalseIncident.setScary(true);
		eveningFalseIncident.setTrafficTime(TrafficTimes.EVENING_NIGHT_MORNING);
		eveningFalseIncident.setWeekDay(WeekDays.WEEKEND);
		eveningFalseIncident.setYear(2000);
		eveningFalseIncident.setIncidentType(IncidentType.PULLING_IN_OUT);

		RideIncident rushHourIncident = new RideIncident();
		rushHourIncident.setScary(false);
		rushHourIncident.setTrafficTime(TrafficTimes.MORNING_RUSH_HOUR);
		rushHourIncident.setWeekDay(WeekDays.WEEKEND);
		rushHourIncident.setYear(2000);
		rushHourIncident.setIncidentType(IncidentType.CLOSE_PASS);

		mockLine.setRideIncident(Arrays.asList(eveningIncident, rushHourIncident, eveningFalseIncident));

		HashBiMap<TrafficTimeWeekDayKey, SafetyMetricsPlanetOsmLine> resultMetrics = analyticsServiceSpy
			.calculateSafetyMetrics(mockLine);

		assertEquals(resultMetrics.size(), 2);

		TrafficTimeWeekDayKey eveningKey = new TrafficTimeWeekDayKey(TrafficTimes.EVENING_NIGHT_MORNING, WeekDays.WEEK, 2000);

		assertEquals(0, resultMetrics.get(eveningKey).getNumberOfNearLeftRightHooks());
		assertEquals(0, resultMetrics.get(eveningKey).getNumberOfHeadOnApproaches());
		assertEquals(0, resultMetrics.get(eveningKey).getNumberOfTailgating());
		assertEquals(0, resultMetrics.get(eveningKey).getNumberOfNearDoorings());
		assertEquals(0, resultMetrics.get(eveningKey).getNumberOfObstacleDodges());
		assertEquals(0, resultMetrics.get(eveningKey).getNumberOfClosePasses());
		assertEquals(1, resultMetrics.get(eveningKey).getNumberOfPullInOuts());

		assertEquals(1, resultMetrics.get(eveningKey).getNumberOfScaryIncidents());
		assertEquals(5, resultMetrics.get(eveningKey).getNumberOfRides());

		assertEquals(0.88f, resultMetrics.get(eveningKey).getDangerousScore(), 0.01);

		TrafficTimeWeekDayKey morningRushHourKey = new TrafficTimeWeekDayKey(TrafficTimes.MORNING_RUSH_HOUR,
				WeekDays.WEEKEND, 2000);

		assertEquals(0, resultMetrics.get(morningRushHourKey).getNumberOfNearLeftRightHooks());
		assertEquals(0, resultMetrics.get(morningRushHourKey).getNumberOfHeadOnApproaches());
		assertEquals(0, resultMetrics.get(morningRushHourKey).getNumberOfTailgating());
		assertEquals(0, resultMetrics.get(morningRushHourKey).getNumberOfNearDoorings());
		assertEquals(0, resultMetrics.get(morningRushHourKey).getNumberOfObstacleDodges());
		assertEquals(0, resultMetrics.get(morningRushHourKey).getNumberOfPullInOuts());
		assertEquals(1, resultMetrics.get(morningRushHourKey).getNumberOfClosePasses());

		assertEquals(0, resultMetrics.get(morningRushHourKey).getNumberOfScaryIncidents());
		assertEquals(10, resultMetrics.get(morningRushHourKey).getNumberOfRides());

		assertEquals(0.1f, resultMetrics.get(morningRushHourKey).getDangerousScore(), 0.01);
	}

	@Test
	public void testUpdateSafetyMetricsHighway() {
		PlanetOsmLine mockStreet1 = new PlanetOsmLine();
		PlanetOsmLine mockStreet2 = new PlanetOsmLine();
		List<PlanetOsmLine> mockStreetsPage1 = Arrays.asList(mockStreet1);
		List<PlanetOsmLine> mockStreetsPage2 = Arrays.asList(mockStreet2);
		List<PlanetOsmLine> emptyPage = new ArrayList<>();

		doReturn(mockStreetsPage1).when(osmHighwayRepository).findAllStreets(PageRequest.of(0, 100));
		doReturn(mockStreetsPage2).when(osmHighwayRepository).findAllStreets(PageRequest.of(1, 100));
		doReturn(emptyPage).when(osmHighwayRepository).findAllStreets(PageRequest.of(2, 100));

		doNothing().when(analyticsServiceSpy).updateSafetyMetrics(anyList());

		analyticsServiceSpy.updateSafetyMetricsHighway();

		verify(osmHighwayRepository, times(3)).findAllStreets(any(PageRequest.class));
		verify(analyticsServiceSpy, times(1)).updateSafetyMetrics(mockStreetsPage1);
		verify(analyticsServiceSpy, times(1)).updateSafetyMetrics(mockStreetsPage2);
	}

	@Test
	public void updateSafetyMetrics_withValidStreets_savesSafetyMetrics() {
		PlanetOsmLine mockStreet = new PlanetOsmLine();
		mockStreet.setId(1L);
		List<PlanetOsmLine> streets = Collections.singletonList(mockStreet);

		SafetyMetricsPlanetOsmLine mockSafetyMetrics = new SafetyMetricsPlanetOsmLine();
		HashBiMap<TrafficTimeWeekDayKey, SafetyMetricsPlanetOsmLine> safetyMetricsMap = HashBiMap.create();
		safetyMetricsMap.put(new TrafficTimeWeekDayKey(TrafficTimes.MORNING_RUSH_HOUR, WeekDays.ALL_WEEK, 2000),
				mockSafetyMetrics);

		doReturn(safetyMetricsMap).when(analyticsServiceSpy).calculateSafetyMetrics(mockStreet);

		analyticsServiceSpy.updateSafetyMetrics(streets);

		verify(safetyMetricsRepository, times(1)).saveAll(anyList());
	}

	@Test
	public void calculateAllDayValues() {
		HashBiMap<TrafficTimeWeekDayKey, SafetyMetricsPlanetOsmLine> metricsMap = HashBiMap.create();
		SafetyMetricsPlanetOsmLine weekMetric = new SafetyMetricsPlanetOsmLine();
		weekMetric.setNumberOfRides(10);
		metricsMap.put(new TrafficTimeWeekDayKey(TrafficTimes.MORNING_RUSH_HOUR, WeekDays.WEEK, 2000), weekMetric);

		SafetyMetricsPlanetOsmLine weekendMetric = new SafetyMetricsPlanetOsmLine();
		weekendMetric.setNumberOfRides(5);
		metricsMap.put(new TrafficTimeWeekDayKey(TrafficTimes.MORNING_RUSH_HOUR, WeekDays.WEEKEND, 2000), weekendMetric);

		analyticsService.calculateAllDayValues(metricsMap, 2000);

		assertEquals(4, metricsMap.size());
		assertEquals(10,
				metricsMap.get(new TrafficTimeWeekDayKey(TrafficTimes.ALL_DAY, WeekDays.WEEK, 2000)).getNumberOfRides());
		assertEquals(5,
				metricsMap.get(new TrafficTimeWeekDayKey(TrafficTimes.ALL_DAY, WeekDays.WEEKEND, 2000)).getNumberOfRides());
	}

	@Test
	public void calculateAllWeekValues() {
		HashBiMap<TrafficTimeWeekDayKey, SafetyMetricsPlanetOsmLine> metricsMap = HashBiMap.create();
		SafetyMetricsPlanetOsmLine weekMetric = new SafetyMetricsPlanetOsmLine();
		weekMetric.setNumberOfRides(10);
		SafetyMetricsPlanetOsmLine weekendMetric = new SafetyMetricsPlanetOsmLine();
		weekendMetric.setNumberOfRides(5);

		metricsMap.put(new TrafficTimeWeekDayKey(TrafficTimes.MORNING_RUSH_HOUR, WeekDays.WEEK, 2000), weekMetric);
		metricsMap.put(new TrafficTimeWeekDayKey(TrafficTimes.MORNING_RUSH_HOUR, WeekDays.WEEKEND, 2000), weekendMetric);

		analyticsService.calculateAllWeekValues(metricsMap, 2000);

		assertEquals(3, metricsMap.size());
		assertEquals(10, metricsMap.get(new TrafficTimeWeekDayKey(TrafficTimes.MORNING_RUSH_HOUR, WeekDays.WEEK, 2000))
			.getNumberOfRides());
		assertEquals(5, metricsMap.get(new TrafficTimeWeekDayKey(TrafficTimes.MORNING_RUSH_HOUR, WeekDays.WEEKEND, 2000))
			.getNumberOfRides());
		assertEquals(15, metricsMap.get(new TrafficTimeWeekDayKey(TrafficTimes.MORNING_RUSH_HOUR, WeekDays.ALL_WEEK, 2000))
			.getNumberOfRides());
	}

}
