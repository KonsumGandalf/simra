package com.simra.konsumgandalf.rides.controllers;

import com.simra.konsumgandalf.common.models.entities.RideIncident;
import com.simra.konsumgandalf.rides.services.RideIncidentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("incidents")
public class RideIncidentController {

	@Autowired
	private RideIncidentService rideIncidentService;

	@GetMapping("street/{id}")
	public Map<String, List<RideIncident>> getIncidentsOfStreets(@PathVariable long id) {
		List<RideIncident> incidents = rideIncidentService.getIncidentsOfStreetSegment(id);
		return Map.of("incidents", incidents);
	}

}
