package com.simra.konsumgandalf.osmrBackend.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

/**
 * This class is the base class for all services that interact with the OSMR backend.
 */
public abstract class OsmrBackendService {

	protected final WebClient webClient;

	protected final Logger logger;

	protected final int DEFAULT_PARTITION_SIZE;

	OsmrBackendService(String osmrEndpoint, int partitionSize, int maxInMemorySize) {
		webClient = WebClient.builder()
			.baseUrl(osmrEndpoint)
			.clientConnector(new ReactorClientHttpConnector(HttpClient.create()))
			.codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(maxInMemorySize))
			.build();

		DEFAULT_PARTITION_SIZE = partitionSize;

		logger = LoggerFactory.getLogger(this.getClass());
	}

	public OsmrBackendService(String osmrEndpoint, int partitionSize) {
		this(osmrEndpoint, partitionSize, 1024);
	}

}
