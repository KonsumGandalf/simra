package com.simra.konsumgandalf.profiles.repositories;

import com.simra.konsumgandalf.common.models.entities.Profile;
import com.simra.konsumgandalf.common.models.entities.SimraRegion;
import com.simra.konsumgandalf.profiles.models.dtos.AggregatedSafetyMetricsDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface ProfileRepository extends JpaRepository<Profile, Long> {

	@Query("SELECT p.lastModified FROM Profile p WHERE p.path = :path")
	Optional<Date> lastModified(String path);

	List<Profile> findBySimraRegion(SimraRegion simraRegion);

	@Query("""
			    SELECT s.groupType AS groupType,
			           sm.groupName AS groupName,
			           SUM(sm.totalRides) AS totalRides,
			           SUM(sm.totalIncidents) AS totalIncidents,
			           SUM(sm.totalScaryIncidents) AS totalScaryIncidents
			    FROM SafetyMetricsProfile sm
			    JOIN sm.simraRegionGroupAssociation s ON sm.groupType = s.groupType
			    AND sm.regionName != "All"
			    GROUP BY s.groupType, sm.groupName
			""")
	List<AggregatedSafetyMetricsDTO> getAggregatedSafetyMetrics();

}
