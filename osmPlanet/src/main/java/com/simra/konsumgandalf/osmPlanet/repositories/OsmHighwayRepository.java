package com.simra.konsumgandalf.osmPlanet.repositories;

import com.simra.konsumgandalf.common.models.entities.PlanetOsmLine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface OsmHighwayRepository extends JpaRepository<PlanetOsmLine, Long> {

	@Query(value = """
			    WITH transformed_point AS (
			        SELECT ST_Transform(ST_SetSRID(ST_MakePoint(:longitude, :latitude), 4326), 3857) AS pt
			    )
			    SELECT
			        id,
			        ST_AsGeoJSON(ST_Transform(ST_Simplify(way, :tolerance), 4326)) as way
			    FROM
			        public.planet_osm_line,
			        transformed_point
			    WHERE
			        (highway IN :roadTypes)
			        AND way && ST_Buffer(transformed_point.pt, :distanceFilter)
			""", nativeQuery = true)
	List<Map<String, Object>> findHighways(@Param("longitude") double longitude, @Param("latitude") double latitude,
			@Param("distanceFilter") int distanceFilter, @Param("roadTypes") List<String> roadTypes,
			@Param("tolerance") double tolerance);

}
