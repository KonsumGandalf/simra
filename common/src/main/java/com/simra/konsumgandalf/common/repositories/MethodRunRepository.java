package com.simra.konsumgandalf.common.repositories;

import com.simra.konsumgandalf.common.models.entities.MethodRun;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MethodRunRepository extends JpaRepository<MethodRun, Long> {

	Optional<MethodRun> findTopByNameOrderByCreatedDateDesc(String name);

}
