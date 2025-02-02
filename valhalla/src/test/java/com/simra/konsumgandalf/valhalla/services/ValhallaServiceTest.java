package com.simra.konsumgandalf.valhalla.services;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ValhallaServiceTest {

	@Test
	void testConstructorLogsCallerClass() {
		ValhallaService valhallaService = new ValhallaService("/test", 100, 1024 * 1024) {
		};

		assertEquals(valhallaService.DEFAULT_PARTITION_SIZE, 100);
		assertThat(valhallaService.logger.getName()).contains("ValhallaServiceTest");
	}

}
