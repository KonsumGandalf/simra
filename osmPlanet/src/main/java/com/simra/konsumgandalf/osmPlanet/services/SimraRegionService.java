package com.simra.konsumgandalf.osmPlanet.services;

import com.simra.konsumgandalf.common.models.entities.Region;
import com.simra.konsumgandalf.common.models.entities.SafetyMetricsSimraRegion;
import com.simra.konsumgandalf.common.models.entities.SimraRegion;
import com.simra.konsumgandalf.common.models.enums.TrafficTimes;
import com.simra.konsumgandalf.common.models.enums.WeekDays;
import com.simra.konsumgandalf.osmPlanet.classes.mapper.SimraRegionMapper;
import com.simra.konsumgandalf.osmPlanet.repositories.SafetyMetricsSimraRegionRepository;
import com.simra.konsumgandalf.osmPlanet.repositories.SimraRegionRepository;
import jakarta.annotation.PostConstruct;
import org.locationtech.jts.geom.Geometry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class SimraRegionService {

	@Autowired
	private SimraRegionRepository simraRegionRepository;

	@Autowired
	private SafetyMetricsSimraRegionRepository safetyMetricsSimraRegionRepository;

	public SimraRegion getRegionByName(String name) {
		Optional<SafetyMetricsSimraRegion> safetyMetricsSimraRegion = safetyMetricsSimraRegionRepository
			.findDistinctByNameAndWeekDayAndTrafficTimeAndYear(name, WeekDays.ALL_WEEK, TrafficTimes.ALL_DAY, 2000);
		Optional<Geometry> regionWayOpt = simraRegionRepository.findWayByName(name);
		List<String> regionNames = simraRegionRepository.findRegionNames(name);
		List<Region> regions = regionNames.stream().map(Region::new).toList();

		SimraRegion sr = new SimraRegion(name);
		sr.setRegions(regions);
		sr.setWay(regionWayOpt.orElse(null));
		sr.setSafetyMetricsSimraRegions(List.of(safetyMetricsSimraRegion.orElse(null)));

		return sr;
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
