package com.simra.konsumgandalf.rides.repositories;

import com.simra.konsumgandalf.common.logging.LogExecutionTime;
import com.simra.konsumgandalf.common.models.entities.PlanetOsmLine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlanetOsmLineRepository extends JpaRepository<PlanetOsmLine, Long> {
    /**
     * Queries all streets that match the given OSM IDs.
     *
     * @param osmIds - A list of OSM IDs.
     * @return A list of streets that match the given OSM IDs.
     */
    @Query("SELECT p FROM PlanetOsmLine p WHERE p.osm_id IN :osmIds")
    public List<PlanetOsmLine> findAllByOsmId(@Param("osmIds") List<Long> osmIds);

}
