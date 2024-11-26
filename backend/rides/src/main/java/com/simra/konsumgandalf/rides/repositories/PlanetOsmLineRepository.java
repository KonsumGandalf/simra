package com.simra.konsumgandalf.rides.repositories;

import com.simra.konsumgandalf.common.models.entities.PlanetOsmLine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlanetOsmLineRepository extends JpaRepository<PlanetOsmLine, Long> {


}
