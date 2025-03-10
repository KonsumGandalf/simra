package com.simra.konsumgandalf.osmPlanet.repositories;

import com.simra.konsumgandalf.common.models.entities.SafetyMetricsRegion;
import com.simra.konsumgandalf.common.models.entities.SafetyMetricsSimraRegion;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SafetyMetricsSimraRegionRepository extends JpaRepository<SafetyMetricsSimraRegion, Long> {
	@EntityGraph(attributePaths = {"region", "region.regions"})
	@Query("""
			SELECT s
			FROM SafetyMetricsSimraRegion s
			WHERE s.region.name = :name
			""")
	Optional<SafetyMetricsSimraRegion> findByName(String name);
}
