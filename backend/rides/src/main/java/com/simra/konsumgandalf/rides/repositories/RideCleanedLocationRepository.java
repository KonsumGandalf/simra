package com.simra.konsumgandalf.rides.repositories;

import com.simra.konsumgandalf.common.models.entities.PlanetOsmLine;
import com.simra.konsumgandalf.common.models.entities.RideCleanedLocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface RideCleanedLocationRepository extends JpaRepository<RideCleanedLocation, Long> {
    @Query(value = """
    INSERT INTO ride_cleaned_location (way, buffered_way)
    WITH points AS (
        SELECT ST_SetSRID(ST_MakePoint(coord.lng, coord.lat), 4326) AS geom
        FROM json_populate_recordset(NULL::record, CAST(:coordinates AS json)) AS coord(lng double precision, lat double precision)
    )
    SELECT 
        ST_MakeLine(geom) AS way,
        ST_Buffer(ST_MakeLine(geom), 0.0001) AS buffered_way
    FROM points
    RETURNING *
    """, nativeQuery = true)
    @Transactional
    RideCleanedLocation createAndSaveGeometry(@Param("coordinates") String coordinates);

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
    List<Long> findNearbyStreets(@Param("rideId") long rideId);
    /*
    @Query(value = """
    INSERT INTO ride_cleaned_location (way)
    WITH points AS (
        SELECT ST_SetSRID(ST_MakePoint(coord.lng, coord.lat), 4326) AS geom
        FROM json_populate_recordset(NULL::record,CAST(:coordinates AS json)) AS coord(lng double precision, lat double precision)
    )
    SELECT ST_MakeLine(geom) AS geometry
    FROM points
    RETURNING *
    """, nativeQuery = true)
    @Transactional
    RideCleanedLocation createAndSaveGeometry(@Param("coordinates") String coordinates);
    @Query(value = """
    SELECT pg_typeof(CAST(:coordinates AS jsonb))
    """, nativeQuery = true)
    Object testType(@Param("coordinates") Object coordinates);

    @Query(value = """
    WITH points AS (
        SELECT ST_SetSRID(ST_MakePoint(c.lng, c.lat), 4326) AS geom
        FROM unnest(:coordinates) AS c
    )
    SELECT ST_MakeLine(geom) AS geometry
    FROM points;
""", nativeQuery = true)
    Geometry createLineStringFromCoordinates(@Param("coordinates") Coordinate[] coordinates);

    @Query(value = """
    WITH points AS (
        SELECT ST_SetSRID(ST_MakePoint(coord.lng, coord.lat), 4326) AS geom
        FROM json_populate_recordset(NULL::record, '[{"lng": 13.4050, "lat": 52.5200}, {"lng": 12.4964, "lat": 41.9028}, {"lng": 12.4964, "lat": 41.9028}]') AS coord(lng double precision, lat double precision)
    )
    SELECT ST_MakeLine(geom) AS geometry
    FROM points;
    """, nativeQuery = true)
    Geometry test(@Param("coordinates") String coordinates);*/
}