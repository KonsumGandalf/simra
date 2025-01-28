package com.simra.konsumgandalf.rides.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.simra.konsumgandalf.common.models.classes.Coordinate;
import com.simra.konsumgandalf.common.models.classes.OsmrMatchInformation;
import com.simra.konsumgandalf.common.models.entities.PlanetOsmLine;
import com.simra.konsumgandalf.common.models.entities.RideEntity;
import com.simra.konsumgandalf.common.models.entities.RideIncident;
import com.simra.konsumgandalf.common.models.classes.RideLocation;
import com.simra.konsumgandalf.common.utils.services.BloomFilterService;
import com.simra.konsumgandalf.common.utils.services.CsvUtilService;
import com.simra.konsumgandalf.common.utils.services.FileReaderService;
import com.simra.konsumgandalf.osmrBackend.services.OsmrBackendMatchService;
import com.simra.konsumgandalf.osmrBackend.services.OsmrBackendNearestService;
import com.simra.konsumgandalf.common.models.maps.IxFunctionToParticipantTypeMap;
import com.simra.konsumgandalf.common.repositories.PlanetOsmLineRepository;
import com.simra.konsumgandalf.rides.repositories.RideEntityRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
@Transactional
public class RideEntityService {

	private static Path dataPath;

	private static final ObjectMapper _objectMapper = new ObjectMapper();

	private static final Logger _logger = LoggerFactory.getLogger(RideEntityService.class);

	@Autowired
	private PlanetOsmLineRepository planetOsmLineRepository;

	@Autowired
	private RideEntityRepository rideEntityRepository;

	@Autowired
	private OsmrBackendMatchService osmrBackendService;

	@Autowired
	private OsmrBackendNearestService osmrBackendNearestService;

	@Autowired
	private CsvUtilService csvUtilService;

	@Autowired
	private FileReaderService fileReaderService;

	@Autowired
	private BloomFilterService bloomFilterService;

	RideEntityService(@Value("${SIMRA_RIDE_FILE_PATH}") String filePath) {
		System.out.println(filePath);
		if (filePath == null) {
			new RideEntityService();
		}
		else {
			dataPath = Paths.get(filePath);
		}
	}

	RideEntityService() {
		dataPath = Paths.get("").toAbsolutePath().resolve("/data").normalize();
	}

	@Async
	public void loadAllPreviousRides() throws Exception {
		List<CompletableFuture<Void>> futures = new ArrayList<>();

		Files.walk(dataPath, 4)
			.filter(Files::isRegularFile)
			.filter(this::isEntityFile)
			.map(Path::toString)
			.filter(this::checkIfRideEntityExists)
			.forEach(path -> {
				CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
					try {
						_logger.info("Processing file: " + path.toString() + " on thread: "
								+ Thread.currentThread().getName());
						generateNewRideEntity(path);
						bloomFilterService.add(path);
					}
					catch (Exception e) {
						_logger.error("Error processing file: " + path.toString(), e);
					}
				});
				futures.add(future);
			});

		CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
	}

	private boolean isEntityFile(Path path) {
		return path.getFileName().toString().startsWith("VM");
	}

	private boolean checkIfRideEntityExists(String path) {
		boolean doesExistInBloomFilter = bloomFilterService.mightContain(path);
		if (doesExistInBloomFilter) {
			_logger.info("[BloomFilter]: Ride entity with path {} already exists", path);
			return false;
		}
		else {
			Optional<RideEntity> rideEntity = rideEntityRepository.findOneByPath(path);
			if (rideEntity.isPresent()) {
				_logger.info("[Database]: Ride entity with path {} already exists", path);
				return false;
			}
			else {
				return true;
			}
		}
	}

	/**
	 * Add the CSV data to the ride entity.
	 * @param rideEntity - The ride entity to enrich
	 * @return - The enriched ride entity
	 */
	// @LogExecutionTime
	protected RideEntity enrichRideEntityWithCsv(RideEntity rideEntity) {
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

		List<RideLocation> rideLocationList = csvUtilService.parseCsvToModel(filteredParts[1], RideLocation.class)
			.stream()
			.filter(this::validateRideLocation)
			.toList();
		rideEntity.setRideLocations(rideLocationList);

		long[] rideTimestamps = rideLocationList.stream()
			.map(RideLocation::getTimeStamp)
			.collect(Collectors.teeing(Collectors.minBy(Long::compareTo), Collectors.maxBy(Long::compareTo),
					(min, max) -> new long[] { min.orElse(0L), max.orElse(0L) }));

		rideEntity.setRideStart(new Date(rideTimestamps[0]));
		rideEntity.setRideEnd(new Date(rideTimestamps[1]));

		List<RideIncident> rideIncidentList = csvUtilService.parseCsvToModel(filteredParts[0], RideIncident.class);
		rideIncidentList = rideIncidentList.stream().map(incident -> {
			IxFunctionToParticipantTypeMap.IxFunctionToParticipantType.forEach((key, value) -> {
				if (key.apply(incident) == 1) {
					incident.addParticipantsInvolved(value);
				}
			});

			if (incident.getTs() != 0) {
				incident.setTimeStamp(new Date(incident.getTs()));
			}
			else {
				rideLocationList.stream()
					.filter(location -> location.getLng() == incident.getLat()
							&& location.getLat() == incident.getLng())
					.findFirst()
					.ifPresentOrElse(location -> {
						incident.setTimeStamp(new Date(location.getTimeStamp()));
					}, () -> incident.setTimeStamp(new Date((rideTimestamps[0] + rideTimestamps[1]) / 2)));
			}

			return incident;
		}).filter(this::validateRideIncident).toList();
		rideEntity.setRideIncidents(rideIncidentList);

		return rideEntity;
	}

	/**
	 * Generate a new ride entity from a CSV file.
	 * @param path - The path to the CSV file
	 * @return - The generated ride entity
	 */
	public RideEntity generateNewRideEntity(String path) {
		RideEntity rideEntity = new RideEntity(path);

		try {
			rideEntity = enrichRideEntityWithCsv(rideEntity);
		}
		catch (IllegalArgumentException e) {
			_logger.error("Error enriching ride entity with CSV", e);
			throw new RuntimeException(e);
		}

		String cleanedRideLocationString = generateCoordinateString(rideEntity.getRideLocations());
		rideEntity.setCoordinates(cleanedRideLocationString);

		rideEntity = linkToPlanetOsmLine(rideEntity);
		rideEntity = linkRideIncidentToPlanetOsmLine(rideEntity);

		return rideEntityRepository.save(rideEntity);
	}

	/**
	 * Creates a cleaned ride location from a ride entity with the help of OSMR and the
	 * planet OSM line repository.
	 * @param rideEntity - The csv enriched ride entity
	 * @return - The cleaned ride location
	 */
	// @LogExecutionTime
	protected RideEntity linkToPlanetOsmLine(RideEntity rideEntity) {
		List<OsmrMatchInformation> coordinates = rideEntity.getRideLocations()
			.stream()
			.map(location -> new OsmrMatchInformation(location.getLng(), location.getLat(),
					location.getTimeStamp() / 1000, location.getAcc()))
			.toList();

		List<Long> waypoints = osmrBackendService.calculateStreetSegmentIdsOfRoute(coordinates);

		List<PlanetOsmLine> streets = planetOsmLineRepository.findAllById(waypoints);

		rideEntity.setPlanetOsmLines(streets);
		return rideEntity;
	}

	protected RideEntity linkRideIncidentToPlanetOsmLine(RideEntity rideEntity) {
		for (RideIncident rideIncident : rideEntity.getRideIncidents()) {
			Coordinate coordinate = new Coordinate(rideIncident);
			if (coordinate.getLat() == 0 || coordinate.getLng() == 0) {
				continue;
			}

			Long id;
			try {
				id = osmrBackendNearestService.getIDNearestStreetToCoordinate(coordinate);
				if (id == null) {
					_logger.error("Could not find nearest street segment to incident");
					continue;
				}

				Optional<PlanetOsmLine> planetOsmLine = planetOsmLineRepository.findById(id);
				if (planetOsmLine.isEmpty()) {
					_logger.error("Could not find planet osm line with id {}", id);
					continue;
				}
				rideIncident.setPlanetOsmLine(planetOsmLine.get());
			}
			catch (Exception e) {
				_logger.error("Error finding nearest street segment to incident", e);
			}
		}

		return rideEntity;
	}

	/**
	 * Create a geometry from a list of ride locations.
	 * @param rideLocationList - A list of ride locations with coordinates
	 * @return - The entity that encapsulates the geometry
	 * @throws JsonProcessingException
	 */
	// @LogExecutionTime
	protected String generateCoordinateString(List<RideLocation> rideLocationList) {
		List<Map<String, Double>> coordinatesList = rideLocationList.stream().map(rideLocation -> {
			Map<String, Double> coordMap = new HashMap<>();
			coordMap.put("lng", rideLocation.getLng());
			coordMap.put("lat", rideLocation.getLat());
			return coordMap;
		}).collect(Collectors.toList());

		try {
			return _objectMapper.writeValueAsString(coordinatesList);
		}
		catch (JsonProcessingException e) {
			return "[]";
		}
	}

	protected boolean validateRideLocation(RideLocation rideLocation) {
		return rideLocation.getLat() != 0 && rideLocation.getLng() != 0 && rideLocation.getTimeStamp() != 0;
	}

	protected boolean validateRideIncident(RideIncident rideIncident) {
		return rideIncident.getLat() != 0 && rideIncident.getLng() != 0;
	}

}
