package com.simra.konsumgandalf.osmPlanet.controller;

import com.simra.konsumgandalf.common.models.entities.PlanetOsmPolygon;
import com.simra.konsumgandalf.common.models.entities.Region;
import com.simra.konsumgandalf.osmPlanet.repositories.OsmPolygonRepository;
import com.simra.konsumgandalf.osmPlanet.services.RegionService;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.io.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/regions")
public class RegionController {

	@Autowired
	private RegionService regionService;

	@GetMapping("/{name}")
	public Optional<Region> getRegionGeometry(@PathVariable String name) {
		return regionService.getRegionByName(name);
	}

}
