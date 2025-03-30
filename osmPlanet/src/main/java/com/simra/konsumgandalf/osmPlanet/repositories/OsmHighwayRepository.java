package com.simra.konsumgandalf.osmPlanet.repositories;

import com.simra.konsumgandalf.common.models.entities.PlanetOsmLine;
import com.simra.konsumgandalf.common.models.enums.TrafficTimes;
import com.simra.konsumgandalf.common.models.enums.WeekDays;
import com.simra.konsumgandalf.common.repositories.PlanetOsmLineRepository;
import com.simra.konsumgandalf.osmPlanet.classes.dtos.FindNumberOfRidesWithinStreetSegmentInTimePeriodDTO;
import com.simra.konsumgandalf.osmPlanet.classes.dtos.RideEntityDTO;
import jakarta.persistence.QueryHint;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.hibernate.jpa.HibernateHints.HINT_FETCH_SIZE;

@Repository
public interface OsmHighwayRepository extends PlanetOsmLineRepository {

	@Query(value = """
			    WITH transformed_point AS (
			                 SELECT ST_Transform(ST_SetSRID(ST_MakePoint(:longitude, :latitude), 4326), 3857) AS pt
			             )
			             SELECT
			                 planet_osm_line.osm_id,
			                 ST_AsGeoJSON(ST_Transform(ST_Simplify(planet_osm_line.way, :tolerance), 4326)) as way,
			                 sm.dangerous_color
			             FROM
			                 public.planet_osm_line
			             JOIN
			                 transformed_point
			                 ON planet_osm_line.way && ST_Buffer(transformed_point.pt, :distanceFilter)
			             LEFT JOIN
			                 safety_metrics_planet_osm_line AS sm
			                 ON planet_osm_line.osm_id = sm.planet_osm_line_osm_id
			                 AND sm.traffic_time = :trafficTime
			                 AND sm.week_day = :weekDay
			                 AND sm.year = :year
			             WHERE
			                 planet_osm_line.highway IN :roadTypes
			                 AND sm.dangerous_color IS NOT NULL;
			""", nativeQuery = true)
	List<Map<String, Object>> findHighways(@Param("longitude") double longitude, @Param("latitude") double latitude,
			@Param("distanceFilter") int distanceFilter, @Param("roadTypes") List<String> roadTypes,
			@Param("tolerance") double tolerance, @Param("trafficTime") String trafficTime,
			@Param("weekDay") String weekDay, @Param("year") int year);

	@EntityGraph(attributePaths = { "rideIncident", "rideEntities" })
	@Query(value = """
			SELECT p
			FROM PlanetOsmLine p
			WHERE p.rideEntities IS NOT EMPTY OR p.rideIncident IS NOT EMPTY
			""")
	List<PlanetOsmLine> findAllStreets(Pageable pageable);

	@Query("""
			    SELECT new com.simra.konsumgandalf.osmPlanet.classes.dtos.FindNumberOfRidesWithinStreetSegmentInTimePeriodDTO(
			        r.trafficTime, r.weekDay, r.year, COUNT(r.id)
			    )
			    FROM PlanetOsmLine p
			    JOIN p.rideEntities r
			    WHERE p.id = :osmId
			    GROUP BY r.weekDay, r.trafficTime, r.year
			""")
	List<FindNumberOfRidesWithinStreetSegmentInTimePeriodDTO> findNumberOfRidesWithinStreetSegmentInTimePeriod(
			Long osmId);

	@EntityGraph(attributePaths = { "rideIncident", "safetyMetricPlanetOsmLines" })
	Optional<PlanetOsmLine> findById(Long id);

	@Query("""
				SELECT r.rideStart as rideStart, r.rideEnd as rideEnd
				FROM PlanetOsmLine p
				JOIN p.rideEntities r
				WHERE p.id = :id
				AND r.rideStart >= :startTime
				AND r.rideEnd <= :endTime
			""")
	List<RideEntityDTO> findRideEntitiesTimeById(Long id, LocalDateTime startTime, LocalDateTime endTime);

	@Query("""
			 	SELECT DISTINCT p.name
				FROM PlanetOsmLine p
				WHERE p.name LIKE :namePrefix%
				ORDER BY p.name
				LIMIT 10
			""")
	List<String> findAllHighwayNameStartingWith(String namePrefix);

	@Query("""
			 	SELECT DISTINCT p.id
				FROM PlanetOsmLine p
				WHERE CAST(p.id AS String) LIKE :idPrefix%
				ORDER BY p.id
				LIMIT 10
			""")
	List<String> findAllHighwayIdStartingWith(String idPrefix);

}
