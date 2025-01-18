package com.simra.konsumgandalf.osmrBackend.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

class OsmrBackendServiceTest {

	@Test
	void testConstructorLogsCallerClass() {
		OsmrBackendService osmrBackendService = new OsmrBackendService("/test", 100, 1024 * 1024) {
		};

		assertEquals(osmrBackendService.DEFAULT_PARTITION_SIZE, 100);
		assertThat(osmrBackendService.logger.getName()).contains("OsmrBackendServiceTest");

	}

}
