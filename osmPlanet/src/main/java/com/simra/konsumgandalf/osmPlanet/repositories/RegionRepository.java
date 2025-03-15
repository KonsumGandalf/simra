package com.simra.konsumgandalf.osmPlanet.repositories;

import com.simra.konsumgandalf.common.models.entities.Region;
import com.simra.konsumgandalf.osmPlanet.classes.dtos.RideEntityTotalDTO;
import org.locationtech.jts.geom.Geometry;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface RegionRepository extends JpaRepository<Region, Long> {

	@EntityGraph(attributePaths = { "safetyMetricsRegions" })
	Optional<Region> findByName(String name);

	Optional<Region> findBasicRegionByName(String name);

	@Query(value = "SELECT ST_AsBinary(ST_Union(g.way)) AS geom FROM (SELECT r.way FROM region r WHERE r.name IN (:names)) AS g",
			nativeQuery = true)
	byte[] unifyRegionWays(List<String> names);

}
