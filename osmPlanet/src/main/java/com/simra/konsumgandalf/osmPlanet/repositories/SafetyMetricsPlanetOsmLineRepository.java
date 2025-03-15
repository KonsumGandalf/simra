package com.simra.konsumgandalf.osmPlanet.repositories;

import com.simra.konsumgandalf.common.models.entities.SafetyMetricsPlanetOsmLine;
import com.simra.konsumgandalf.common.models.enums.TrafficTimes;
import com.simra.konsumgandalf.common.models.enums.WeekDays;
import com.simra.konsumgandalf.osmPlanet.classes.dtos.RegionSafetyMetricsProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SafetyMetricsPlanetOsmLineRepository
		extends JpaRepository<SafetyMetricsPlanetOsmLine, Long>, JpaSpecificationExecutor<SafetyMetricsPlanetOsmLine> {

	@Query(value = """
			SELECT s
			FROM SafetyMetricsPlanetOsmLine s
			WHERE s.planetOsmLine.id = :id
			AND s.trafficTime = :trafficTime
			AND s.weekDay = :weekDay
			AND s.year = :year
			""")
	Optional<SafetyMetricsPlanetOsmLine> findByStreetId(long id, TrafficTimes trafficTime, WeekDays weekDay, int year);

	@Query("""
			    SELECT
			    	b.osmId AS osmId,
			    	b.name AS name,
			    	b.adminLevel AS adminLevel,
			    	sm.trafficTime as trafficTime,
			    	sm.weekDay AS weekDay,
			    	sm.year AS year,
			    	SUM(sm.numberOfIncidents) AS totalIncidents,
			    	SUM(sm.numberOfScaryIncidents) AS totalScaryIncidents,
			    	SUM(sm.numberOfClosePasses) AS totalClosePasses,
			    	SUM(sm.numberOfPullInOuts) AS totalPullInOuts,
			    	SUM(sm.numberOfNearLeftRightHooks) AS totalNearLeftRightHooks,
			    	SUM(sm.numberOfHeadOnApproaches) AS totalHeadOnApproaches,
			    	SUM(sm.numberOfTailgating) AS totalTailgating,
			    	SUM(sm.numberOfNearDoorings) AS totalNearDoorings,
			    	SUM(sm.numberOfObstacleDodges) AS totalObstacleDodges
			    FROM SafetyMetricsPlanetOsmLine sm
			    JOIN sm.planetOsmLine pol
			    JOIN PlanetOsmPolygon b ON ST_Contains(b.way, pol.way)
			    WHERE b.boundary = 'administrative'
			    AND b.adminLevel IN (:adminLevel)
			    GROUP BY b.osmId, b.name, b.adminLevel, sm.trafficTime, sm.weekDay, sm.year
			""")
	List<RegionSafetyMetricsProjection> getRegionSafetyMetricsOfAdminLevel(List<Integer> adminLevel);

}
