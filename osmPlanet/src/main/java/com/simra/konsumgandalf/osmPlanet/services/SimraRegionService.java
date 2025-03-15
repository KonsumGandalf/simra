package com.simra.konsumgandalf.osmPlanet.services;

import com.simra.konsumgandalf.common.models.entities.Region;
import com.simra.konsumgandalf.common.models.entities.SimraRegion;
import com.simra.konsumgandalf.osmPlanet.repositories.SimraRegionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SimraRegionService {

	@Autowired
	private SimraRegionRepository simraRegionRepository;

	public Optional<SimraRegion> getRegionByName(String name) {
		Optional<SimraRegion> simraRegion = simraRegionRepository.findByName(name);
		if (simraRegion.isPresent()) {
			for (Region r : simraRegion.get().getRegions()) {
				r.setWay(null);
			}
		}
		return simraRegion;
	}

}
