package com.simra.konsumgandalf.common.services;

import com.simra.konsumgandalf.common.models.entities.MethodRun;
import com.simra.konsumgandalf.common.repositories.MethodRunRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * This service provides insight about the last run of a method.
 */
@Service
public class MethodRunService {

	@Autowired
	private MethodRunRepository methodRunRepository;

	public Optional<MethodRun> getLastRunOfRun(String name) {
		return methodRunRepository.findTopByNameOrderByCreatedDateDesc(name);
	}

}
