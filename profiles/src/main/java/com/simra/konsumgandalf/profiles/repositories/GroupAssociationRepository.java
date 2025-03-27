package com.simra.konsumgandalf.profiles.repositories;

import com.simra.konsumgandalf.common.models.entities.SimraRegion;
import com.simra.konsumgandalf.common.models.entities.SimraRegionGroupAssociation;
import com.simra.konsumgandalf.common.models.enums.SafetyMetricsProfileGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface GroupAssociationRepository extends JpaRepository<SimraRegionGroupAssociation, Long> {

	@Modifying
	@Query("DELETE FROM SimraRegionGroupAssociation s WHERE s.simraRegion.name = :name")
	void deleteAllBySimraRegion(String name);

	@Query("SELECT s FROM SimraRegionGroupAssociation s WHERE s.simraRegion.name = :name")
	Optional<SimraRegionGroupAssociation[]> findBySimraRegion(String name);

	Optional<SimraRegionGroupAssociation> findBySimraRegionAndGroupType(SimraRegion simraRegion,
			SafetyMetricsProfileGroup groupType);

}
