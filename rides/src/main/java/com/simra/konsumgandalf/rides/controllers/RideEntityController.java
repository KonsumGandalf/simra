package com.simra.konsumgandalf.rides.controllers;

import com.simra.konsumgandalf.rides.services.RideEntityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/rides")
public class RideEntityController {

	@Autowired
	private RideEntityService rideEntityService;

	@Async
	@PostMapping("/bloom-filter")
	public void loadAllPreviousRides() throws Exception {
		rideEntityService.loadAllPreviousRidesBloomFilter();
	}

	@Async
	@PostMapping("/database-filter")
	public void loadAllPreviousRidesDatabase() throws Exception {
		rideEntityService.loadAllPreviousRidesDatabase();
	}

	@GetMapping("geometries/{id}")
	public Map<String, String[]> getRideGeometries(@PathVariable long id) {
		return rideEntityService.getRideGeometries(id);
	}

}
