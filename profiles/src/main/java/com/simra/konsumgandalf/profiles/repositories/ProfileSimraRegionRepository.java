package com.simra.konsumgandalf.profiles.repositories;

import com.simra.konsumgandalf.common.models.entities.SimraRegion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProfileSimraRegionRepository extends JpaRepository<SimraRegion, Long> {

	Optional<SimraRegion> findByName(String name);

}
