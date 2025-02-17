package com.simra.konsumgandalf.osmPlanet.repositories;

import com.simra.konsumgandalf.common.models.entities.PlanetOsmLine;
import com.simra.konsumgandalf.common.repositories.PlanetOsmLineRepository;
import com.simra.konsumgandalf.osmPlanet.classes.dtos.FindNumberOfRidesWithinStreetSegmentInTimePeriodDTO;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface OsmHighwayRepository extends PlanetOsmLineRepository {

	@Query(value = """
			    WITH transformed_point AS (
			                 SELECT ST_Transform(ST_SetSRID(ST_MakePoint(:longitude, :latitude), 4326), 3857) AS pt
			             )
			             SELECT
			                 planet_osm_line.osm_id,
			                 ST_AsGeoJSON(ST_Transform(ST_Simplify(planet_osm_line.way, :tolerance), 4326)) as way,
			                 safety_metrics.dangerous_color
			             FROM
			                 public.planet_osm_line
			             JOIN
			                 transformed_point
			                 ON planet_osm_line.way && ST_Buffer(transformed_point.pt, :distanceFilter)
			             LEFT JOIN
			                 safety_metrics
			                 ON planet_osm_line.osm_id = safety_metrics.planet_osm_line_osm_id
			                 AND safety_metrics.traffic_time = :trafficTime
			                 AND safety_metrics.week_day = :weekDay -- Only join when dangerous_color is not null
			             WHERE
			                 planet_osm_line.highway IN :roadTypes
			                 AND safety_metrics.dangerous_color IS NOT NULL;
			""", nativeQuery = true)
	List<Map<String, Object>> findHighways(@Param("longitude") double longitude, @Param("latitude") double latitude,
			@Param("distanceFilter") int distanceFilter, @Param("roadTypes") List<String> roadTypes,
			@Param("tolerance") double tolerance, @Param("trafficTime") String trafficTime,
			@Param("weekDay") String weekDay);

	@EntityGraph(attributePaths = { "rideIncident" })
	@Query(value = """
			SELECT p
			FROM PlanetOsmLine p
			WHERE p.highway != '' AND
				(p.rideEntities IS NOT EMPTY OR p.rideIncident IS NOT EMPTY)
			""")
	List<PlanetOsmLine> findAllStreets(Pageable pageable);

	@Query("""
			    SELECT new com.simra.konsumgandalf.osmPlanet.classes.dtos.FindNumberOfRidesWithinStreetSegmentInTimePeriodDTO(
			        r.weekDay, r.trafficTime, COUNT(r.id)
			    )
			    FROM PlanetOsmLine p
			    JOIN p.rideEntities r
			    WHERE p.id = :osmId
			    GROUP BY r.weekDay, r.trafficTime
			""")
	List<FindNumberOfRidesWithinStreetSegmentInTimePeriodDTO> findNumberOfRidesWithinStreetSegmentInTimePeriod(
			@Param("osmId") Long osmId);

	}
