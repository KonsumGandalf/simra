package com.simra.konsumgandalf.osmPlanet.services;

import com.simra.konsumgandalf.common.models.entities.Region;
import com.simra.konsumgandalf.osmPlanet.repositories.RegionRepository;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKBReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RegionService {

	@Autowired
	private RegionRepository regionRepository;

	public Optional<Region> getRegionByName(String name) {
		return regionRepository.findBasicRegionByName(name);
	}

}
