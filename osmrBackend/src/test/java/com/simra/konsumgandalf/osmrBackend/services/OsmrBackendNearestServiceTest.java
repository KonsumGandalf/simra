package com.simra.konsumgandalf.osmrBackend.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.spy;

import com.simra.konsumgandalf.common.models.classes.Coordinate;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;

@ExtendWith(MockitoExtension.class)
class OsmrBackendNearestServiceTest {

	@InjectMocks
	private OsmrBackendNearestService osmrBackendNearestService;

	OsmrBackendNearestService osmrBackendNearestSpy;

	@BeforeEach
	public void setUp() {
		osmrBackendNearestSpy = spy(osmrBackendNearestService);
	}

	@Nested
	class TestGetIDNearestStreetToCoordinate {

		private MockWebServer mockWebServer;

		private OsmrBackendNearestService osmr;

		@BeforeEach
		public void setup() throws IOException {
			mockWebServer = new MockWebServer();
			mockWebServer.start();
			String baseUrl = mockWebServer.url("/").toString();
			osmr = new OsmrBackendNearestService(baseUrl);
		}

		@AfterEach
		public void tearDown() throws IOException {
			mockWebServer.shutdown();
		}

		@Test
		public void testGetIDNearestStreetToCoordinate_CorrectResponse() throws Exception {
			Coordinate coordinate = new Coordinate(52.520007, 13.404954);
			String jsonResponse = "{\"waypoints\":[{\"hint\":\"\",\"location\":[13.404954,52.520007],\"name\":\"503096340\"}]}";
			mockWebServer
				.enqueue(new MockResponse().setBody(jsonResponse).addHeader("Content-Type", "application/json"));
			Long id = osmr.getIDNearestStreetToCoordinate(coordinate);

			assertEquals(503096340, id);
		}

		@Test
		public void testGetIDNearestStreetToCoordinate_NullResponse() throws Exception {
			Coordinate coordinate = new Coordinate(52.520007, 13.404954);
			String jsonResponse = "{\"code\":404}";
			mockWebServer
				.enqueue(new MockResponse().setBody(jsonResponse).addHeader("Content-Type", "application/json"));
			try {
				osmr.getIDNearestStreetToCoordinate(coordinate);
			}
			catch (Exception e) {
				assertEquals("No suitable Street found", e.getMessage());
			}
		}

	}

}
