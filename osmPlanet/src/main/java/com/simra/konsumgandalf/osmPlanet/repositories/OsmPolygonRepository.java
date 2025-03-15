package com.simra.konsumgandalf.osmPlanet.repositories;

import com.simra.konsumgandalf.common.models.entities.PlanetOsmPolygon;
import com.simra.konsumgandalf.common.models.enums.TrafficTimes;
import com.simra.konsumgandalf.common.models.enums.WeekDays;
import com.simra.konsumgandalf.osmPlanet.classes.dtos.RideEntityTotalDTO;
import org.locationtech.jts.geom.Geometry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OsmPolygonRepository extends JpaRepository<PlanetOsmPolygon, Long> {

	/**
	 * Returns the length of all rides in a region
	 */
	@Query("""
				SELECT SUM(ST_LENGTH_M(r.way)) AS totalDistance,
				SUM(1) AS totalRides
				FROM PlanetOsmPolygon b
				JOIN RideEntity r
				ON r.trafficTime IN :trafficTime
				AND r.weekDay IN :weekDay
				AND r.year IN :year
				AND ST_INTERSECTS(b.way, ST_Transform(r.way, 3857))
				WHERE b.osmId = :osmId
				AND b.boundary = 'administrative'
			""")
	RideEntityTotalDTO totalRidesByRegion(Long osmId, List<TrafficTimes> trafficTime, List<WeekDays> weekDay,
			List<Integer> year);

	/**
	 * Returns the length of all street segments in a region
	 */
	@Query("""
				SELECT AVG(CAST(ST_LENGTH_M(ST_TRANSFORM(p.way, 4326)) AS double)) AS avgSegmentDistance
				FROM PlanetOsmPolygon b
				JOIN PlanetOsmLine p
				ON ST_INTERSECTS(b.way, ST_Transform(p.way, 3857))
				WHERE b.osmId = :osmId
			""")
	Float getAvgSegmentDistance(Long osmId);

	@Query("""
				SELECT b.way
				FROM PlanetOsmPolygon b
				WHERE b.osmId = :osmId
				ORDER BY ST_AREA(b.way) DESC
			 		LIMIT 1
			""")
	Geometry getWayByOsmId(Long osmId);

}
