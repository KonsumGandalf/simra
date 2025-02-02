package com.simra.konsumgandalf.rides.repositories;

import com.simra.konsumgandalf.common.models.entities.PlanetOsmLine;
import com.simra.konsumgandalf.common.models.entities.RideIncident;
import org.hibernate.query.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface RideIncidentRepository extends JpaRepository<RideIncident, Long> {

	@Query("SELECT r FROM RideIncident r WHERE r.planetOsmLine.id = :planetOsmLineId")
	List<RideIncident> getRideIncidentsByPlanetOsmLineId(Long planetOsmLineId);

}
