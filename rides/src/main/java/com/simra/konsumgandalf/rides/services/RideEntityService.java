package com.simra.konsumgandalf.rides.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.simra.konsumgandalf.common.models.classes.OsmrMatchInformation;
import com.simra.konsumgandalf.common.models.classes.RideLocation;
import com.simra.konsumgandalf.common.models.entities.PlanetOsmLine;
import com.simra.konsumgandalf.common.models.entities.RideEntity;
import com.simra.konsumgandalf.common.models.entities.RideIncident;
import com.simra.konsumgandalf.common.models.enums.IncidentType;
import com.simra.konsumgandalf.common.models.maps.IxFunctionToParticipantTypeMap;
import com.simra.konsumgandalf.common.repositories.PlanetOsmLineRepository;
import com.simra.konsumgandalf.common.utils.services.BloomFilterService;
import com.simra.konsumgandalf.common.utils.services.CsvUtilService;
import com.simra.konsumgandalf.common.utils.services.FileReaderService;
import com.simra.konsumgandalf.rides.repositories.RideEntityRepository;
import com.simra.konsumgandalf.valhalla.services.ValhallaTraceAttributesService;
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

import static com.simra.konsumgandalf.common.constants.AppDates.FALLBACK_DATE;
import static com.simra.konsumgandalf.common.constants.AppDates.START_OF_RECORDING;

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
	private ValhallaTraceAttributesService valhallaTraceAttributesService;

	@Autowired
	private CsvUtilService csvUtilService;

	@Autowired
	private FileReaderService fileReaderService;

	@Autowired
	private BloomFilterService bloomFilterService;

	RideEntityService(@Value("${SIMRA_RIDE_FILE_PATH:./}") String filePath) {
		dataPath = Paths.get(filePath);
	}

	@Async
	public void loadAllPreviousRides() throws Exception {
		List<CompletableFuture<Void>> futures = new ArrayList<>();

		Files.walk(dataPath, 4)
			.filter(Files::isRegularFile)
			.filter(FileReaderService::isEntityFile)
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

	private boolean checkIfRideEntityExists(String path) {
		boolean doesExistInBloomFilter = bloomFilterService.mightContain(path);
		if (!doesExistInBloomFilter) {
			_logger.info("[BloomFilter]: Ride entity with path {} already exists", path);
			return true;
		}
		else {
			Optional<RideEntity> rideEntity = rideEntityRepository.findOneByPath(path);
			if (rideEntity.isPresent()) {
				_logger.info("[Database]: Ride entity with path {} already exists", path);
				bloomFilterService.add(path);
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
		rideIncidentList = rideIncidentList.stream()
			.filter(incident -> incident.getIncidentType() != IncidentType.DUMMY_INCIDENT)
			.map(incident -> {
				IxFunctionToParticipantTypeMap.IxFunctionToParticipantType.forEach((key, value) -> {
					if (key.apply(incident) == 1) {
						incident.addParticipantsInvolved(value);
					}
				});

				Date dateOfIncident = getTimeStampFromRideIncident(incident, rideLocationList, rideTimestamps,
						rideEntity.getPath());
				incident.setTimeStamp(dateOfIncident);

				return incident;
			})
			.filter(this::validateRideIncident)
			.toList();

		if (rideIncidentList.size() > 6) {
			_logger.warn("No valid incidents found in ride entity with path {}", rideEntity.getPath());
		}

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

		return rideEntityRepository.save(rideEntity);
	}

	/**
	 * Links a ride entity and its incidents to the closest street segments in the planet
	 * OSM line repository.
	 * @param rideEntity - The csv enriched ride entity
	 * @return - The cleaned ride location
	 */
	// @LogExecutionTime
	protected RideEntity linkToPlanetOsmLine(RideEntity rideEntity) {
		List<OsmrMatchInformation> coordinates = rideEntity.getRideLocations()
			.stream()
			.map(location -> new OsmrMatchInformation(location.getLng(), location.getLat(),
					location.getTimeStamp() / 1000))
			.toList();

		List<Long> streetSegmentIdsOfRoute = valhallaTraceAttributesService
			.calculateStreetSegmentIdsOfRoute(coordinates);
		if (streetSegmentIdsOfRoute.isEmpty()) {
			_logger.error("Could not find any street segments for ride entity with path {}", rideEntity.getPath());
			return rideEntity;
		}

		List<PlanetOsmLine> streets = planetOsmLineRepository.findAllById(streetSegmentIdsOfRoute);
		if (streets.isEmpty()) {
			_logger.error("Could not find any street segments for ride entity with path {}", rideEntity.getPath());
			return rideEntity;
		}
		rideEntity.setPlanetOsmLines(streets);

		for (RideIncident incident : rideEntity.getRideIncidents()) {
			PlanetOsmLine planetOsmLine = planetOsmLineRepository.findClosestStreetSegments(streetSegmentIdsOfRoute,
					incident.getLng(), incident.getLat());
			incident.setPlanetOsmLine(planetOsmLine);
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

	/**
	 * This method tries to find the timestamp of a ride incident with multiple
	 * strategies.
	 * @param incident - The ride incident
	 * @param rideLocationList - The list of ride locations of the same ride as the
	 * incident
	 * @param rideTimestamps - The start and end timestamps of the ride
	 * @param ridePath - The path to the ride file
	 * @return - The timestamp of the ride incident
	 */
	protected Date getTimeStampFromRideIncident(RideIncident incident, List<RideLocation> rideLocationList,
			long[] rideTimestamps, String ridePath) {
		Date incidentDate = new Date(incident.getTs());
		if (isAfterStartOfRecording(incidentDate)) {
			return incidentDate;
		}

		Optional<Date> matchedDate = rideLocationList.stream()
			.filter(location -> location.getLng() == incident.getLng() && location.getLat() == incident.getLat())
			.findFirst()
			.map(location -> new Date(location.getTimeStamp()));

		if (matchedDate.isPresent() && isAfterStartOfRecording(matchedDate.get())) {
			return matchedDate.get();
		}

		if (rideTimestamps.length == 2) {
			incidentDate = new Date((rideTimestamps[0] + rideTimestamps[1]) / 2);
		}

		if (!isAfterStartOfRecording(incidentDate)) {
			try {
				incidentDate = fileReaderService.getFileLastModified(ridePath);
			}
			catch (Exception e) {
				_logger.error("Error getting last modified date of file", e);
			}
		}

		if (!isAfterStartOfRecording(incidentDate)) {
			incidentDate = FALLBACK_DATE;
		}

		return incidentDate;
	}

	protected boolean validateRideLocation(RideLocation rideLocation) {
		return rideLocation.getLat() != 0 && rideLocation.getLng() != 0 && rideLocation.getTimeStamp() != 0;
	}

	protected boolean validateRideIncident(RideIncident rideIncident) {
		return rideIncident.getLat() != 0 && rideIncident.getLng() != 0;
	}

	public Map<String, String[]> getRideGeometries(long id) {
		return rideEntityRepository.findRideGeometries(id);
	}

	private boolean isAfterStartOfRecording(Date date) {
		return (date != null) && date.after(START_OF_RECORDING);
	}

}
