package com.simra.konsumgandalf.common.repositories;

import com.simra.konsumgandalf.common.models.entities.PlanetOsmLine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PlanetOsmLineRepository extends JpaRepository<PlanetOsmLine, Long> {

	@Query("""
			SELECT COUNT(re)
			FROM PlanetOsmLine pl
			JOIN pl.rideEntities re
			WHERE pl.id = :id
			AND re.rideStart >= :rideStart
			AND re.rideEnd <= :rideEnd
			  """)
	long countRidesWithinTimeRange(@Param("id") long id, @Param("rideStart") long rideStart,
			@Param("rideEnd") long rideEnd);

}
