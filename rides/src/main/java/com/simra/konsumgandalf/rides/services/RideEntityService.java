package com.simra.konsumgandalf.rides.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.simra.konsumgandalf.common.models.classes.OsmrMatchInformation;
import com.simra.konsumgandalf.common.models.entities.PlanetOsmLine;
import com.simra.konsumgandalf.common.models.entities.RideCleanedLocation;
import com.simra.konsumgandalf.common.models.entities.RideEntity;
import com.simra.konsumgandalf.common.models.entities.RideIncident;
import com.simra.konsumgandalf.common.models.entities.RideLocation;
import com.simra.konsumgandalf.common.utils.services.CsvUtilService;
import com.simra.konsumgandalf.common.utils.services.FileReaderService;
import com.simra.konsumgandalf.osmrBackend.services.OsmrBackendService;
import com.simra.konsumgandalf.rides.repositories.PlanetOsmLineRepository;
import com.simra.konsumgandalf.rides.repositories.RideCleanedLocationRepository;
import com.simra.konsumgandalf.rides.repositories.RideEntityRepository;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RideEntityService {

	private static final ObjectMapper _objectMapper = new ObjectMapper();

	private static final Logger _logger = LoggerFactory.getLogger(RideEntityService.class);

	@Autowired
	private RideEntityRepository rideEntityRepository;

	@Autowired
	private RideCleanedLocationRepository rideCleanedLocationRepository;

	@Autowired
	private PlanetOsmLineRepository planetOsmLineRepository;

	@Autowired
	private OsmrBackendService osmrBackendService;

	@Autowired
	private CsvUtilService csvUtilService;

	@Autowired
	private FileReaderService fileReaderService;

	/**
	 * Add the CSV data to the ride entity.
	 * @param rideEntity - The ride entity to enrich
	 * @return - The enriched ride entity
	 */
	public RideEntity enrichRideEntityWithCsv(RideEntity rideEntity) {
		String content = fileReaderService.readFileFromPath(rideEntity.getPath());

		String[] filteredParts = Arrays.stream(content.split("=+"))
			.map(part -> Arrays.stream(part.split("\n"))
				.filter(line -> !line.contains("#"))
				.collect(Collectors.joining("\n"))
				.trim())
			.toArray(String[]::new);

		if (filteredParts.length < 2) {
			throw new IllegalArgumentException("File does not contain two CSV sections");
		}

		List<RideIncident> rideIncidentList = csvUtilService.parseCsvToModel(filteredParts[0], RideIncident.class);
		rideEntity.setRideIncidents(rideIncidentList);

		List<RideLocation> rideLocationList = csvUtilService.parseCsvToModel(filteredParts[1], RideLocation.class);
		rideEntity.setRideLocation(rideLocationList);

		return rideEntity;
	}

	public RideEntity generateNewRideEntity(String path) {
		return generateNewRideEntity(path, true);
	}

	/**
	 * Generate a new ride entity from a CSV file.
	 * @param path - The path to the CSV file
	 * @param createGeometry - Whether to create the geometry from the ride locations
	 * @return - The generated ride entity
	 */
	public RideEntity generateNewRideEntity(String path, boolean createGeometry) {
		RideEntity rideEntity = new RideEntity(path);

		try {
			rideEntity = enrichRideEntityWithCsv(rideEntity);
		}
		catch (IllegalArgumentException e) {
			_logger.error("Error enriching ride entity with CSV", e);
			throw new RuntimeException(e);
		}

		RideCleanedLocation cleanedRideLocation;

		if (createGeometry) {
			try {
				cleanedRideLocation = createGeometryFromRideLocations(rideEntity.getRideLocation());
			}
			catch (JsonProcessingException e) {
				_logger.error("Error creating geometry from ride locations", e);
				throw new RuntimeException(e);
			}
		}
		else {
			cleanedRideLocation = rideCleanedLocationRepository.save(new RideCleanedLocation());
		}

		List<OsmrMatchInformation> coordinates = rideEntity.getRideLocation()
			.stream()
			.filter(location -> location.getLat() != 0 && location.getLng() != 0 && location.getTimeStamp() != 0)
			.map(location -> new OsmrMatchInformation(location.getLng(), location.getLat(),
					location.getTimeStamp() / 1000, location.getAcc()))
			.collect(Collectors.toList());

		List<Long> waypoints = osmrBackendService.calculateStreetSegmentOsmIdsOfRoute(coordinates);

		List<PlanetOsmLine> streets = planetOsmLineRepository.findAllByOsmId(waypoints);

		for (PlanetOsmLine street : streets) {
			street.getRideCleanedLocations().add(cleanedRideLocation);
		}
		cleanedRideLocation.setPlanetOsmLines(streets);
		RideCleanedLocation cleanedRideLocationSaved = rideCleanedLocationRepository.save(cleanedRideLocation);

		rideEntity.setRideCleanedIncident(cleanedRideLocationSaved);
		return rideEntityRepository.save(rideEntity);
	}

	/**
	 * Create a geometry from a list of ride locations.
	 * @param rideLocationList - A list of ride locations with coordinates
	 * @return - The entity that encapsulates the geometry
	 * @throws JsonProcessingException
	 */
	public RideCleanedLocation createGeometryFromRideLocations(List<RideLocation> rideLocationList)
			throws JsonProcessingException {
		List<Map<String, Double>> coordinatesList = rideLocationList.stream()
			.filter(coord -> coord.getLng() != 0 && coord.getLat() != 0)
			.map(rideLocation -> {
				Map<String, Double> coordMap = new HashMap<>();
				coordMap.put("lng", rideLocation.getLng());
				coordMap.put("lat", rideLocation.getLat());
				return coordMap;
			})
			.collect(Collectors.toList());

		String coordinatesJson = _objectMapper.writeValueAsString(coordinatesList);
		return rideCleanedLocationRepository.createAndSaveGeometry(coordinatesJson);
	}

	// temporary method to test the functionality
	public List<RideCleanedLocation> findAllCleanedRides(long id) {
		return rideCleanedLocationRepository.findAllById(Collections.singleton(id));
	}

}
