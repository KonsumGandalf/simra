package com.simra.konsumgandalf.common.repositories;

import com.simra.konsumgandalf.common.models.entities.PlanetOsmLine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlanetOsmLineRepository extends JpaRepository<PlanetOsmLine, Long> {

	@Query(value = """
				SELECT *
					FROM planet_osm_line
					WHERE osm_id IN (:streetSegmentIds)
					ORDER BY way <-> ST_Transform(ST_SetSRID(ST_MakePoint(:lng, :lat), 4326), 3857)
					LIMIT 1;
			""", nativeQuery = true)
	PlanetOsmLine findClosestStreetSegments(@Param("streetSegmentIds") List<Long> streetSegmentIds,
			@Param("lng") double lng, @Param("lat") double lat);

}
