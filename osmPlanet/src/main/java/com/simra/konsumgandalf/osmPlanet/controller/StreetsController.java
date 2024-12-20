package com.simra.konsumgandalf.osmPlanet.controller;

import com.simra.konsumgandalf.osmPlanet.services.OsmHighwayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("streets")
public class StreetsController {

	@Autowired
	private OsmHighwayService osmHighwayService;

	@GetMapping("")
	public List<Map<String, Object>> getHighwayInformation(@RequestParam("lat") double lat,
			@RequestParam("lng") double lng, @RequestParam("zoom") int zoom) {
		return osmHighwayService.getHighwayInformation(lat, lng, zoom);
	}

}
