package com.simra.konsumgandalf.osmPlanet.services;

import com.simra.konsumgandalf.common.models.entities.Region;
import com.simra.konsumgandalf.common.models.entities.SimraRegion;
import com.simra.konsumgandalf.osmPlanet.classes.mapper.SimraRegionMapper;
import com.simra.konsumgandalf.osmPlanet.repositories.SimraRegionRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

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

	@PostConstruct
	public void createSimraRegions() {
		List<SimraRegion> simraRegions = simraRegionRepository.findAll();
		Set<String> simraRegionNames = new SimraRegionMapper().map.keySet();

		List<SimraRegion> toBeCreated = new ArrayList<>();
		for (String simraRegionName : simraRegionNames) {
			if (simraRegions.stream().noneMatch(simraRegion -> simraRegion.getName().equals(simraRegionName))) {
				toBeCreated.add(new SimraRegion(simraRegionName));
			}
		}
		simraRegionRepository.saveAll(toBeCreated);
	}

}
