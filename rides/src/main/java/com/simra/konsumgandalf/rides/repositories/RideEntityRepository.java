package com.simra.konsumgandalf.rides.repositories;

import com.simra.konsumgandalf.common.models.entities.RideEntity;
import org.springframework.context.annotation.DependsOn;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RideEntityRepository extends JpaRepository<RideEntity, Long> {

	@Query("SELECT r FROM RideEntity r WHERE r.path = :rideId")
	public Optional<RideEntity> findOneByPath(String rideId);

}
