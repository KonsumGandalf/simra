package com.simra.konsumgandalf.rides.repositories;

import com.simra.konsumgandalf.common.models.entities.RideLocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RideLocationsRepository extends JpaRepository<RideLocation, Long> {

}
