package com.simra.konsumgandalf.rides.services;

import com.simra.konsumgandalf.common.models.entities.RideIncident;
import com.simra.konsumgandalf.rides.repositories.RideIncidentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RideIncidentService {

	@Autowired
	private RideIncidentRepository rideIncidentRepository;

	public List<RideIncident> getIncidentsOfStreetSegment(long id) {
		return this.rideIncidentRepository.getRideIncidentsByPlanetOsmLineId(id);
	}

}
