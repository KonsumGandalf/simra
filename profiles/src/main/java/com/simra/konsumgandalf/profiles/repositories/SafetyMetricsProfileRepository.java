package com.simra.konsumgandalf.profiles.repositories;

import com.simra.konsumgandalf.common.models.entities.SafetyMetricsProfile;
import com.simra.konsumgandalf.common.models.enums.SafetyMetricsProfileGroup;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SafetyMetricsProfileRepository extends JpaRepository<SafetyMetricsProfile, Long> {

	@Transactional
	@Modifying
	@Query("DELETE FROM SafetyMetricsProfile s WHERE s.simraRegionGroupAssociation.simraRegion.name = :name")
	void deleteAllBySimraRegion(String name);

}
