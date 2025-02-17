package com.simra.konsumgandalf.osmPlanet.repositories;

import com.simra.konsumgandalf.common.models.entities.SafetyMetrics;
import com.simra.konsumgandalf.common.models.enums.TrafficTimes;
import com.simra.konsumgandalf.common.models.enums.WeekDays;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SafetyMetricsRepository
		extends JpaRepository<SafetyMetrics, Long>, JpaSpecificationExecutor<SafetyMetrics> {

	@Query(value = """
			SELECT s
			FROM SafetyMetrics s
			WHERE s.planetOsmLine.id = :id
			AND s.trafficTime = :trafficTime
			AND s.weekDay = :weekDay
			""")
	Optional<SafetyMetrics> findByStreetId(long id, TrafficTimes trafficTime, WeekDays weekDay);

}
