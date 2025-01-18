package com.simra.konsumgandalf.osmPlanet.services;

import com.google.common.collect.HashBiMap;
import com.simra.konsumgandalf.common.models.entities.PlanetOsmLine;
import com.simra.konsumgandalf.common.models.entities.RideEntity;
import com.simra.konsumgandalf.common.models.entities.RideIncident;
import com.simra.konsumgandalf.common.models.entities.SafetyMetrics;
import com.simra.konsumgandalf.common.models.enums.IncidentType;
import com.simra.konsumgandalf.common.models.enums.TrafficTimes;
import com.simra.konsumgandalf.common.models.enums.WeekDays;
import com.simra.konsumgandalf.osmPlanet.classes.dtos.FindNumberOfRidesWithinStreetSegmentInTimePeriodDTO;
import com.simra.konsumgandalf.osmPlanet.classes.keys.TrafficTimeWeekDayKey;
import com.simra.konsumgandalf.osmPlanet.repositories.OsmHighwayRepository;
import com.simra.konsumgandalf.osmPlanet.repositories.SafetyMetricsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
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
	private SafetyMetricsRepository safetyMetricsRepository;

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
				WeekDays.WEEKEND, TrafficTimes.EARLY_RUSH_HOUR, 1);
		morningRushHourRideDTO.setNumberOfRides(10);
		when(osmHighwayRepository.findNumberOfRidesWithinStreetSegmentInTimePeriod(any(Long.class)))
			.thenReturn(Collections.singletonList(morningRushHourRideDTO));

		RideIncident eveningIncident = new RideIncident();
		eveningIncident.setScary(true);
		eveningIncident.setTrafficTime(TrafficTimes.EVENING_NIGHT_MORNING);
		eveningIncident.setWeekDay(WeekDays.WEEK);
		eveningIncident.setIncident(IncidentType.PULLING_IN_OUT);

		RideIncident rushHourIncident = new RideIncident();
		rushHourIncident.setScary(false);
		rushHourIncident.setTrafficTime(TrafficTimes.EARLY_RUSH_HOUR);
		rushHourIncident.setWeekDay(WeekDays.WEEKEND);
		rushHourIncident.setIncident(IncidentType.CLOSE_PASS);

		mockLine.setRideIncident(Arrays.asList(eveningIncident, rushHourIncident));

		doNothing().when(analyticsServiceSpy).calculateAllWeekValues(any());
		doNothing().when(analyticsServiceSpy).calculateAllDayValues(any());

		HashBiMap<TrafficTimeWeekDayKey, SafetyMetrics> resultMetrics = analyticsServiceSpy
			.calculateSafetyMetrics(mockLine);

		assertEquals(resultMetrics.size(), 2);

		TrafficTimeWeekDayKey eveningKey = new TrafficTimeWeekDayKey(TrafficTimes.EVENING_NIGHT_MORNING, WeekDays.WEEK);

		assertEquals(0, resultMetrics.get(eveningKey).getNumberOfNearLeftRightHooks());
		assertEquals(0, resultMetrics.get(eveningKey).getNumberOfHeadOnApproaches());
		assertEquals(0, resultMetrics.get(eveningKey).getNumberOfTailgating());
		assertEquals(0, resultMetrics.get(eveningKey).getNumberOfNearDoorings());
		assertEquals(0, resultMetrics.get(eveningKey).getNumberOfObstacleDodges());
		assertEquals(0, resultMetrics.get(eveningKey).getNumberOfClosePasses());
		assertEquals(1, resultMetrics.get(eveningKey).getNumberOfPullInOuts());

		assertEquals(1, resultMetrics.get(eveningKey).getNumberOfScaryIncidents());
		assertEquals(1, resultMetrics.get(eveningKey).getNumberOfRides());

		assertEquals(4.4f, resultMetrics.get(eveningKey).getDangerousScore(), 0.01);

		TrafficTimeWeekDayKey morningRushHourKey = new TrafficTimeWeekDayKey(TrafficTimes.EARLY_RUSH_HOUR,
				WeekDays.WEEKEND);

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
	public void testUpdateHighwayInformation() {
		PlanetOsmLine mockStreet1 = new PlanetOsmLine();
		PlanetOsmLine mockStreet2 = new PlanetOsmLine();
		List<PlanetOsmLine> mockStreetsPage1 = Arrays.asList(mockStreet1);
		List<PlanetOsmLine> mockStreetsPage2 = Arrays.asList(mockStreet2);
		List<PlanetOsmLine> emptyPage = new ArrayList<>();

		doReturn(mockStreetsPage1).when(osmHighwayRepository).findAllStreets(PageRequest.of(0, 100));
		doReturn(mockStreetsPage2).when(osmHighwayRepository).findAllStreets(PageRequest.of(1, 100));
		doReturn(emptyPage).when(osmHighwayRepository).findAllStreets(PageRequest.of(2, 100));

		doNothing().when(analyticsServiceSpy).updateSafetyMetrics(anyList());

		analyticsServiceSpy.updateHighwayInformation();

		verify(osmHighwayRepository, times(3)).findAllStreets(any(PageRequest.class));
		verify(analyticsServiceSpy, times(1)).updateSafetyMetrics(mockStreetsPage1);
		verify(analyticsServiceSpy, times(1)).updateSafetyMetrics(mockStreetsPage2);
	}

	@Test
	public void updateSafetyMetrics_withValidStreets_savesSafetyMetrics() {
		PlanetOsmLine mockStreet = new PlanetOsmLine();
		mockStreet.setId(1L);
		List<PlanetOsmLine> streets = Collections.singletonList(mockStreet);

		SafetyMetrics mockSafetyMetrics = new SafetyMetrics();
		HashBiMap<TrafficTimeWeekDayKey, SafetyMetrics> safetyMetricsMap = HashBiMap.create();
		safetyMetricsMap.put(new TrafficTimeWeekDayKey(TrafficTimes.EARLY_RUSH_HOUR, WeekDays.ALL_WEEK),
				mockSafetyMetrics);

		doReturn(safetyMetricsMap).when(analyticsServiceSpy).calculateSafetyMetrics(mockStreet);

		analyticsServiceSpy.updateSafetyMetrics(streets);

		verify(safetyMetricsRepository, times(1)).saveAll(anyList());
	}

	@Test
	public void calculateAllDayValues() {
		HashBiMap<TrafficTimeWeekDayKey, SafetyMetrics> metricsMap = HashBiMap.create();
		SafetyMetrics weekMetric = new SafetyMetrics();
		weekMetric.setNumberOfRides(10);
		metricsMap.put(new TrafficTimeWeekDayKey(TrafficTimes.EARLY_RUSH_HOUR, WeekDays.WEEK), weekMetric);

		SafetyMetrics weekendMetric = new SafetyMetrics();
		weekendMetric.setNumberOfRides(5);
		metricsMap.put(new TrafficTimeWeekDayKey(TrafficTimes.EARLY_RUSH_HOUR, WeekDays.WEEKEND), weekendMetric);

		analyticsService.calculateAllDayValues(metricsMap);

		assertEquals(5, metricsMap.size());
		assertEquals(10,
				metricsMap.get(new TrafficTimeWeekDayKey(TrafficTimes.ALL_DAY, WeekDays.WEEK)).getNumberOfRides());
		assertEquals(5,
				metricsMap.get(new TrafficTimeWeekDayKey(TrafficTimes.ALL_DAY, WeekDays.WEEKEND)).getNumberOfRides());
		assertEquals(15,
				metricsMap.get(new TrafficTimeWeekDayKey(TrafficTimes.ALL_DAY, WeekDays.ALL_WEEK)).getNumberOfRides());
	}

	@Test
	public void calculateAllWeekValues() {
		HashBiMap<TrafficTimeWeekDayKey, SafetyMetrics> metricsMap = HashBiMap.create();
		SafetyMetrics weekMetric = new SafetyMetrics();
		weekMetric.setNumberOfRides(10);
		SafetyMetrics weekendMetric = new SafetyMetrics();
		weekendMetric.setNumberOfRides(5);

		metricsMap.put(new TrafficTimeWeekDayKey(TrafficTimes.EARLY_RUSH_HOUR, WeekDays.WEEK), weekMetric);
		metricsMap.put(new TrafficTimeWeekDayKey(TrafficTimes.EARLY_RUSH_HOUR, WeekDays.WEEKEND), weekendMetric);

		analyticsService.calculateAllWeekValues(metricsMap);

		assertEquals(6, metricsMap.size());
		assertEquals(10, metricsMap.get(new TrafficTimeWeekDayKey(TrafficTimes.EARLY_RUSH_HOUR, WeekDays.WEEK))
			.getNumberOfRides());
		assertEquals(5, metricsMap.get(new TrafficTimeWeekDayKey(TrafficTimes.EARLY_RUSH_HOUR, WeekDays.WEEKEND))
			.getNumberOfRides());
		assertEquals(15, metricsMap.get(new TrafficTimeWeekDayKey(TrafficTimes.EARLY_RUSH_HOUR, WeekDays.ALL_WEEK))
			.getNumberOfRides());
	}

}
