package com.simra.konsumgandalf.osmPlanet.repositories;

import com.simra.konsumgandalf.common.models.entities.SafetyMetricsPlanetOsmLine;
import com.simra.konsumgandalf.common.models.entities.SafetyMetricsRegion;
import com.simra.konsumgandalf.common.models.enums.TrafficTimes;
import com.simra.konsumgandalf.common.models.enums.WeekDays;
import com.simra.konsumgandalf.osmPlanet.classes.dtos.RegionSafetyMetricsProjection;
import org.checkerframework.checker.units.qual.A;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SafetyMetricsRegionRepository
		extends JpaRepository<SafetyMetricsRegion, Long>, JpaSpecificationExecutor<SafetyMetricsRegion> {

	@Query("""
			SELECT s
			FROM SafetyMetricsRegion s
			WHERE s.region.name = :name
			""")
	List<SafetyMetricsRegion> findByName(String name);

}
