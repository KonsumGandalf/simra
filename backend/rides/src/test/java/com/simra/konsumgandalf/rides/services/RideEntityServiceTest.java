package com.simra.konsumgandalf.rides.services;

import com.simra.konsumgandalf.common.models.entities.RideEntity;
import com.simra.konsumgandalf.common.models.entities.RideIncident;
import com.simra.konsumgandalf.common.models.entities.RideLocation;
import com.simra.konsumgandalf.rides.repositories.PlanetOsmLineRepository;
import com.simra.konsumgandalf.rides.repositories.RideCleanedLocationRepository;
import com.simra.konsumgandalf.rides.repositories.RideEntityRepository;
import com.simra.konsumgandalf.common.utils.services.CsvUtilService;
import com.simra.konsumgandalf.common.utils.services.FileReaderService;
import org.assertj.core.util.Arrays;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RideEntityServiceTest {

    @Mock
    private RideEntityRepository rideEntityRepository;

    @Mock
    private RideCleanedLocationRepository rideCleanedLocationRepository;

    @Mock
    private PlanetOsmLineRepository planetOsmLineRepository;

    @Mock
    private FileReaderService fileReaderService;

    @Mock
    private CsvUtilService csvUtilService;

    @InjectMocks
    private RideEntityService rideEntityService;

    @Nested
    class EnrichRideEntityWithCsv {
        RideEntity mockRideEntity;

        @Test
        public void testEnrichRideEntityWithCsv_ValidFile() throws Exception {
            mockRideEntity = new RideEntity("valid.csv");
            when(fileReaderService.readFileFromPath("valid.csv")).thenReturn("bike,incident\n1,3\n====\nlat,lng\n1.0,4.0");
            when(csvUtilService.parseCsvToModel("bike,incident\n1,3", RideIncident.class)).thenReturn(new ArrayList<>());
            when(csvUtilService.parseCsvToModel("lat,lng\n1.0,4.0", RideLocation.class)).thenReturn(new ArrayList<>());

            RideEntity result = rideEntityService.enrichRideEntityWithCsv(mockRideEntity);

            // Verify that the repository save method was called
            // Verify the order of method calls
            InOrder inOrder = inOrder(csvUtilService);
            inOrder.verify(csvUtilService).parseCsvToModel("bike,incident\n1,3", RideIncident.class);
            inOrder.verify(csvUtilService).parseCsvToModel("lat,lng\n1.0,4.0", RideLocation.class);

            assertEquals(result, mockRideEntity);
        }

        @Test
        public void testEnrichRideEntityWithCsv_InvalidFile() {
            mockRideEntity = new RideEntity("invalid.csv");
            when(fileReaderService.readFileFromPath("invalid.csv")).thenReturn("manual1,manual2\nvalue1,value2");

            // Verify that an exception is thrown
            assertThrows(IllegalArgumentException.class, () -> {
                rideEntityService.enrichRideEntityWithCsv(mockRideEntity);
            });

            // Verify that the repository save method was not called
            verify(csvUtilService, times(0)).parseCsvToModel(any(String.class), eq(RideIncident.class));
        }
    }

    @Nested
    class GenerateNewRideEntity {
        @Test
        public void testGenerateNewRideEntity_Valid() throws Exception {
            RideEntity mockRideEntity = new RideEntity("valid.csv");
            RideLocation mockRideLocation1 = new RideLocation();
            mockRideLocation1.setLat(1.0);
            mockRideLocation1.setLng(2.0);
            when(fileReaderService.readFileFromPath("valid.csv")).thenReturn("bike,incident\n1,3\n====\nlat,lng\n1.0,4.0");
            when(csvUtilService.parseCsvToModel("bike,incident\n1,3", RideIncident.class)).thenReturn(new ArrayList<>());
            when(csvUtilService.parseCsvToModel("lat,lng\n1.0,4.0", RideLocation.class)).thenReturn(Collections.singletonList(mockRideLocation1));

            RideEntity result = rideEntityService.generateNewRideEntity("valid.csv");

            verify(rideEntityService, times(1)).enrichRideEntityWithCsv(mockRideEntity);
            verify(rideEntityService, times(1)).createGeometryFromRideLocations(Collections.singletonList(mockRideLocation1));
            verify(rideCleanedLocationRepository, times(1)).findNearbyStreets(any(Long.class));
            verify(planetOsmLineRepository, times(1)).findAllById(any(List.class));
            verify(rideEntityRepository, times(1)).save(mockRideEntity);

            assertEquals(result, mockRideEntity);
        }

        @Test
        public void testGenerateNewRideEntity_InvalidCsvFile() {
            RideEntity mockRideEntity = new RideEntity("invalid.csv");
            when(rideEntityService.enrichRideEntityWithCsv(mockRideEntity)).thenThrow(new IllegalArgumentException());

            assertThrows(RuntimeException.class, () -> {
                rideEntityService.generateNewRideEntity(mockRideEntity.getPath());
            });
        }

        @Test
        public void testGenerateNewRideEntity_JsonProcessingException() throws Exception {
            RideEntity mockRideEntity = new RideEntity("valid.csv");
            when(rideEntityService.enrichRideEntityWithCsv(mockRideEntity)).thenReturn(mockRideEntity);
            when(rideEntityService.createGeometryFromRideLocations(Collections.singletonList(new RideLocation()))).thenThrow(new IllegalArgumentException());

            assertThrows(RuntimeException.class, () -> {
                rideEntityService.generateNewRideEntity(mockRideEntity.getPath());
            });
        }
    }

    @Nested
    class CreateGeometryFromRideLocations{
        @Test
        public void testCreateGeometryFromRideLocations() throws Exception {
            List<RideLocation> rideLocationList = new ArrayList<>();

            RideLocation mockRideLocation1 = new RideLocation();
            mockRideLocation1.setLat(1.0);
            mockRideLocation1.setLng(2.0);
            rideLocationList.add(mockRideLocation1);

            RideLocation mockRideLocation2 = new RideLocation();
            mockRideLocation2.setLat(3.0);
            mockRideLocation2.setLng(4.0);
            rideLocationList.add(mockRideLocation2);

            String expectedGeometry = "[{\"lng\":2.0,\"lat\":1.0},{\"lng\":4.0,\"lat\":3.0}]";

            rideEntityService.createGeometryFromRideLocations(rideLocationList);

            // Verify that the repository save method was called
            verify(rideCleanedLocationRepository, times(1)).createAndSaveGeometry(expectedGeometry);
        }
    }
}