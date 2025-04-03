package com.simra.konsumgandalf.rides.services;

import com.simra.konsumgandalf.common.constants.CronExpressions;
import com.simra.konsumgandalf.common.models.entities.PlanetOsmLine;
import com.simra.konsumgandalf.common.repositories.PlanetOsmLineRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Transactional
@Service
public class PlanetOsmLineService {

	@Autowired
	private PlanetOsmLineRepository planetOsmLineRepository;

	private Set<Long> modifiedHighwayIds = ConcurrentHashMap.newKeySet();

	public void addModifiedHighways(List<PlanetOsmLine> streets) {
		this.modifiedHighwayIds.addAll(streets.stream().map(PlanetOsmLine::getId).toList());
	}

	@Scheduled(cron = CronExpressions.EVERY_15_MINUTES)
	public void updateHighways() {
		if (modifiedHighwayIds.size() < 10_000) {
			return;
		}

		updateModifiedHighways();
	}

	public void updateModifiedHighways() {
		planetOsmLineRepository.updateLastModifiedByIds(modifiedHighwayIds, Instant.now());
		modifiedHighwayIds.clear();
	}

}
