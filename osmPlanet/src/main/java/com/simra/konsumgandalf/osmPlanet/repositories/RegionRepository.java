package com.simra.konsumgandalf.osmPlanet.repositories;

import com.simra.konsumgandalf.common.models.entities.Region;
import org.locationtech.jts.geom.Geometry;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface RegionRepository extends JpaRepository<Region, Long> {

	@EntityGraph(attributePaths = { "safetyMetricsRegions" })
	Optional<Region> findByName(String name);

	@Query("SELECT r.way FROM Region r WHERE r.name = :name")
	Optional<Geometry> findRegionWayByName(String name);

	Optional<Region> findBasicRegionByName(String name);

	@Query(value = "SELECT ST_AsBinary(ST_Union(g.way)) AS geom FROM (SELECT r.way FROM region r WHERE r.name IN (:names)) AS g",
			nativeQuery = true)
	byte[] unifyRegionWays(List<String> names);

	@Query("SELECT r.name FROM Region r WHERE r.name ILIKE :prefix% AND r.safetyMetricsRegions IS NOT EMPTY")
	List<String> findAllNames(String prefix);

	@Query(value = """
			    WITH transformed_point AS (
			                 SELECT ST_SetSRID(ST_MakePoint(:longitude, :latitude), 4326) AS pt
			    )
			    SELECT
			        region.name AS name,
			        ST_AsGeoJSON(ST_Simplify(region.way, :tolerance), 4326) as way,
			        sm.dangerous_color
			    FROM
			        region
			    JOIN
			        transformed_point
			        ON region.way && ST_Buffer(transformed_point.pt, :distanceFilter)
			    LEFT JOIN
			        safety_metrics_region AS sm
			        ON region.name = sm.region_name
			        AND (region.admin_level = :adminLevel OR region.admin_level = 9 AND :adminLevel = 6)
			        AND sm.traffic_time = :trafficTime
			        AND sm.week_day = :weekDay
			        AND sm.year = :year
			    WHERE sm.dangerous_color IS NOT NULL;
			""", nativeQuery = true)
	List<Map<String, Object>> findWays(int adminLevel, double longitude, double latitude, int distanceFilter,
			double tolerance, String trafficTime, String weekDay, int year);

	@Query(value = """
				SELECT
					r.name,
					r.admin_level,
					ST_AsGeoJSON(r.way) AS way
				FROM region r
			""", nativeQuery = true)
	List<Map<String, Object>> getPolygonRaw();

}
