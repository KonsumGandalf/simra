package com.simra.konsumgandalf.rides.repositories;

import com.simra.konsumgandalf.common.models.entities.PlanetOsmLine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlanetOsmLineRepository extends JpaRepository<PlanetOsmLine, Long> {
    @Query("SELECT p FROM PlanetOsmLine p WHERE p.osm_id IN :osmIds")
    public List<PlanetOsmLine> findAllByOsmId(List<Long> osmIds);

}
