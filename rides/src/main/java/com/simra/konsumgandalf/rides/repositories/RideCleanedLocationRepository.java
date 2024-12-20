package com.simra.konsumgandalf.rides.repositories;

import com.simra.konsumgandalf.common.models.entities.RideCleanedLocation;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface RideCleanedLocationRepository extends JpaRepository<RideCleanedLocation, Long> {

	/**
	 * Construct a geometry from a list of coordinates and save it to the database.
	 * @param coordinates - A list of coordinates in the form of a JSON string.
	 * @return The entity that encapsulates the geometry.
	 */
	@Query(value = """
			INSERT INTO ride_cleaned_location (way)
			WITH points AS (
			    SELECT ST_SetSRID(ST_MakePoint(coord.lng, coord.lat), 4326) AS geom
			    FROM json_populate_recordset(NULL::record, CAST(:coordinates AS json)) AS coord(lng double precision, lat double precision)
			)
			SELECT
			    ST_MakeLine(geom) AS way
			FROM points
			RETURNING *
			""",
			nativeQuery = true)
	@Transactional
	RideCleanedLocation createAndSaveGeometry(@Param("coordinates") String coordinates);

	/**
	 * Find nearby streets for a given ride.
	 * @deprecated This method is deprecated and should not be used.
	 * @param rideId
	 * @return
	 */
	@Query(value = """
			        WITH ride_buffer AS (
			    SELECT
			        r.id AS ride_id,
			        r.buffered_way AS ride_buffer
			    FROM
			        ride_cleaned_location r
			    WHERE
			        r.id = :rideId
			),
			nearby_streets AS (
			    SELECT
			        p.id
			    FROM
			        planet_osm_line p
			    JOIN
			        ride_buffer r ON ST_Within(ST_Transform(p.way, 4326), r.ride_buffer)
			    WHERE
			        p.highway IS NOT NULL
			        AND (
			            p.highway != 'footway'
			            OR (p.tags->'cycleway' IS NOT NULL AND p.tags->'cycleway' != 'yes')
			            OR (p.tags->'oneway:bicycle' = 'yes')
			        )
			        AND NOT (
			            p.highway = 'secondary'
			            AND COALESCE(p.tags->'cycleway:both', '') = 'separate'
			        )
			        AND p.highway != 'service'
			)
			SELECT
			    *
			FROM
			    nearby_streets;
			""", nativeQuery = true)
	@Transactional
	@Deprecated
	List<Long> findNearbyStreets(@Param("rideId") long rideId);

}
