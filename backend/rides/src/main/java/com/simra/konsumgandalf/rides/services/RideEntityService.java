package com.simra.konsumgandalf.rides.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.simra.konsumgandalf.common.models.entities.*;
import com.simra.konsumgandalf.rides.repositories.PlanetOsmLineRepository;
import com.simra.konsumgandalf.rides.repositories.RideCleanedLocationRepository;
import com.simra.konsumgandalf.rides.repositories.RideEntityRepository;
import com.simra.konsumgandalf.common.utils.services.CsvUtilService;
import com.simra.konsumgandalf.common.utils.services.FileReaderService;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

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
    private CsvUtilService csvUtilService;

    @Autowired
    private FileReaderService fileReaderService;

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

        List<RideIncident> rideIncidentList = csvUtilService.parseCsvToModel(filteredParts[0], RideIncident.class);
        rideEntity.setRideIncidents(rideIncidentList);

        List<RideLocation> rideLocationList = csvUtilService.parseCsvToModel(filteredParts[1], RideLocation.class);
        rideEntity.setRideLocation(rideLocationList);

        return rideEntity;
    }

    public RideEntity generateNewRideEntity(String path){
        RideEntity rideEntity = new RideEntity(path);

        try {
            rideEntity = enrichRideEntityWithCsv(rideEntity);
        } catch (IllegalArgumentException e) {
            _logger.error("Error enriching ride entity with CSV", e);
            throw new RuntimeException(e);
        }

        RideCleanedLocation cleanedRideLocation;
        try {
            cleanedRideLocation = createGeometryFromRideLocations(rideEntity.getRideLocation());
        } catch (JsonProcessingException e) {
            _logger.error("Error creating geometry from ride locations", e);
            throw new RuntimeException(e);
        }

        List<Long> result = rideCleanedLocationRepository.findNearbyStreets(cleanedRideLocation.getId());
        List<PlanetOsmLine> streets = planetOsmLineRepository.findAllById(result);

        for (PlanetOsmLine street : streets) {
            street.getRideCleanedLocations().add(cleanedRideLocation);
        }
        cleanedRideLocation.setPlanetOsmLines(streets);

        rideEntity.setRideCleanedIncident(cleanedRideLocation);
        return rideEntityRepository.save(rideEntity);
    }

    protected RideCleanedLocation createGeometryFromRideLocations(List<RideLocation> rideLocationList) throws JsonProcessingException {
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
