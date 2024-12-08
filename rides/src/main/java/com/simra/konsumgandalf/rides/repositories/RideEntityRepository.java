package com.simra.konsumgandalf.rides.repositories;

import com.simra.konsumgandalf.common.models.entities.RideEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RideEntityRepository extends JpaRepository<RideEntity, Long> {
    }
