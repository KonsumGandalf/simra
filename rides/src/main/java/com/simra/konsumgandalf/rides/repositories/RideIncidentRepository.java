package com.simra.konsumgandalf.rides.repositories;

import com.simra.konsumgandalf.common.models.entities.PlanetOsmLine;
import com.simra.konsumgandalf.common.models.entities.RideIncident;
import com.simra.konsumgandalf.rides.models.dtos.RideIncidentDTO;
import jakarta.persistence.Tuple;
import org.hibernate.query.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Repository
public interface RideIncidentRepository extends JpaRepository<RideIncident, Long> {

	@Query("SELECT r FROM RideIncident r WHERE r.planetOsmLine.id = :planetOsmLineId")
	List<RideIncident> getRideIncidentsByPlanetOsmLineId(Long planetOsmLineId);

	// TODO Returning a string would be faster
	@Query(value = """
			SELECT id, lat, lng, scary
			FROM ride_incident
			WHERE ST_DWithin(way, ST_SetSRID(ST_MakePoint(:lng, :lat), 4326), :radius)
			""", nativeQuery = true)
	List<Map<String, Object>> getAllIncidentsWithRange(double lng, double lat, double radius);

}
