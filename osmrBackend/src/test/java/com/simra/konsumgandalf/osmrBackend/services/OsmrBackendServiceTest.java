package com.simra.konsumgandalf.osmrBackend.services;

import com.simra.konsumgandalf.common.models.classes.Coordinate;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class OsmrBackendServiceTest {
    @InjectMocks
    private OsmrBackendService osmrBackendService;

    OsmrBackendService osmrBackendSpy;

    @BeforeEach
    public void setUp() {
        osmrBackendSpy = spy(osmrBackendService);
    }


    @Test
    public void testCalculateStreetSegmentOsmIdsOfRoute() {
        ArrayList<Coordinate> coordinates = new ArrayList<>();
        for (int i = 0; i <= 101; i++) {
            coordinates.add(new Coordinate(52.520007, 13.404954));
        }

        Mono<List<Long>> mockIds = Mono.just(Collections.singletonList(1L));
        doReturn(mockIds).when(osmrBackendSpy).fetchStepsFromChunk(anyList());

        List<Long> ids = osmrBackendSpy.calculateStreetSegmentOsmIdsOfRoute(coordinates);

        verify(osmrBackendSpy, times(2)).fetchStepsFromChunk(anyList());
        verify(osmrBackendSpy, times(1)).combineStepChunks(anyList());
        // Its 1 because distinct() in combineStepChunks
        assertEquals(1, ids.size());
    }

    @Test
    public void testCombineStepChunks() {
        List<List<Long>> stepChunks = new ArrayList<>();
        stepChunks.add(Collections.singletonList(1L));
        stepChunks.add(Collections.singletonList(2L));

        List<Long> combined = osmrBackendService.combineStepChunks(stepChunks);

        assertEquals(2, combined.size());
        assertEquals(1L, combined.get(0));
        assertEquals(2L, combined.get(1));
    }

    @Nested
    class TestFetchStepsFromChunk {
        private MockWebServer mockWebServer;
        private OsmrBackendService osmrBackendService;

        @BeforeEach
        public void setUp() throws IOException {
            mockWebServer = new MockWebServer();
            mockWebServer.start();
            String baseUrl = mockWebServer.url("/").toString();
            osmrBackendService = new OsmrBackendService(baseUrl);
        }

        @AfterEach
        public void tearDown() throws IOException {
            mockWebServer.shutdown();
        }

        @Test
        public void testFetchStepsFromChunk_ChunkSizing() throws InterruptedException {
            ArrayList<Coordinate> chunk = new ArrayList<>();
            chunk.add(new Coordinate(52.520007, 13.404954));
            chunk.add(new Coordinate(42.520007, 23.404954));

            String jsonResponse = "{ \"matchings\": [ { \"confidence\": 0.9, \"legs\": [ { \"steps\": [ { \"name\": 1 }, { \"name\": 2 } ] } ] } ] }";
            mockWebServer.enqueue(new MockResponse().setBody(jsonResponse).addHeader("Content-Type", "application/json"));

            List<Long> ids = osmrBackendService.fetchStepsFromChunk(chunk).block();

            RecordedRequest request = mockWebServer.takeRequest();
            assertEquals("/52.520007,13.404954;42.520007,23.404954?steps=true", request.getPath());
            assertEquals(List.of(1L, 2L), ids);
        }
    }
}
