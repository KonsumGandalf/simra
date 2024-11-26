package com.simra.konsumgandalf.repositories;

import com.simra.konsumgandalf.common.models.entities.PlanetOsmLine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OsmStreetRepository extends JpaRepository<PlanetOsmLine, Long> {


}
