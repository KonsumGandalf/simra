package com.simra.konsumgandalf.rides.repositories;

import com.simra.konsumgandalf.common.models.entities.RideIncident;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RideManualRepository extends JpaRepository<RideIncident, Long> {

}
