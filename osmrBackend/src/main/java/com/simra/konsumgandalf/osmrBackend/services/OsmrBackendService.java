package com.simra.konsumgandalf.osmrBackend.services;

import com.google.common.collect.Lists;
import com.simra.konsumgandalf.common.models.classes.OsmrMatchInformation;
import com.simra.konsumgandalf.osmrBackend.models.OsmrLeg;
import com.simra.konsumgandalf.osmrBackend.models.OsmrMatching;
import com.simra.konsumgandalf.osmrBackend.models.OsmrMatchingResponse;
import com.simra.konsumgandalf.osmrBackend.models.OsmrStep;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This service interacts with the OSMR backend.
 */
@Service
public class OsmrBackendService {

	private static final Logger _logger = LoggerFactory.getLogger(OsmrBackendService.class);

	private static final int DEFAULT_PARTITION_SIZE = 300;

	private final WebClient _webClient;

	public OsmrBackendService(@Value("${OSMR_BACKEND_MATCH_URL}") String osmrBackendUrl) {
		_webClient = WebClient.builder()
			.baseUrl(osmrBackendUrl)
			.clientConnector(new ReactorClientHttpConnector(HttpClient.create()))
			.codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(10 * 1024 * 1024))
			.build();
	}

	/**
	 * This helper method calculates the radius of the circle around the coordinates.
	 * @param accuracy - The accuracy of the coordinates of the tracking device
	 * @return - The radius of the circle around the coordinates
	 */
	private static String calculateRadius(double accuracy) {
		double base = 50;
		double growthRate = -0.25;

		double result = base / (1 + Math.exp(-growthRate * (accuracy - 5)));

		return String.format("%.2f", Math.max(5, result));
	}

	/**
	 * This method recognizes the street segments for a list of coordinates. The method
	 * partitions the list of coordinates into chunks of 100 coordinates each since
	 * osmr-backend sets a default limit of 100 coordinates. The partitions are then
	 * processed concurrently.
	 * @param coordinates - The list of coordinates of a route
	 * @return - The id of the street segments of the route
	 */
	public List<Long> calculateStreetSegmentOsmIdsOfRoute(List<OsmrMatchInformation> coordinates) {
		List<List<OsmrMatchInformation>> partitions = Lists.partition(coordinates, DEFAULT_PARTITION_SIZE);

		return Flux.fromIterable(partitions)
			.flatMap(this::fetchStepsWithRetry)
			.collectList()
			.map(this::combineStepChunks)
			.onErrorResume(ex -> {
				_logger.warn("Error during calculation of street segments: {}", ex.getMessage()); // Minimal
																									// error
																									// logging
				return Mono.just(Collections.emptyList());
			})
			.block();
	}

	/**
	 * This method ensures that the fetching of the street segments of the route is
	 * retried in case of an error.
	 * @param chunk - The subsegment of coordinates of a route which will be futher
	 * segmented to reduce the data loss in case of an error
	 * @return - The ids of the street segments of the route
	 */
	private Mono<List<Long>> fetchStepsWithRetry(List<OsmrMatchInformation> chunk) {
		return fetchStepsFromChunk(chunk).onErrorResume(WebClientResponseException.class, ex -> {

			if (ex.getStatusCode() == HttpStatus.BAD_REQUEST
					&& ex.getResponseBodyAsString().contains("Could not find a matching segment for any coordinate.")) {
				return retryWithSmallerPartitions(chunk, chunk.size());
			}

			_logger.error("Error fetching steps for chunk: {} - HTTP Status: {}", chunk, ex.getStatusCode()); // Minimal
																												// log
																												// message
			return Mono.just(Collections.emptyList()); // Return empty list on error
		});
	}

	/**
	 * This method divides the chunk of coordinates into smaller partitions and retries
	 * the fetching of the street segments of the route.
	 * @param chunk
	 * @param partitionSize
	 * @return
	 */
	private Mono<List<Long>> retryWithSmallerPartitions(List<OsmrMatchInformation> chunk, int partitionSize) {
		if (partitionSize < 10) {
			_logger.warn("Final retry attempt reached with partition size: {}", partitionSize);
			return Mono.just(Collections.emptyList());
		}

		_logger.info("Retrying with smaller partition size: {}", partitionSize);

		List<List<OsmrMatchInformation>> subPartitions = Lists.partition(chunk, partitionSize / 2);
		return Flux.fromIterable(subPartitions)
			.flatMap(this::fetchStepsFromChunk)
			.collectList()
			.map(this::combineStepChunks)
			.onErrorResume(WebClientResponseException.class, ex -> {
				_logger.error("Error fetching steps for chunk: {} - {}", chunk, ex.getMessage());
				return retryWithSmallerPartitions(chunk, partitionSize / 2);
			});
	}

	/**
	 * This method fetches from the OSMR backend and returns the ids of the steps (street
	 * segments) of the route.
	 * @param chunk - The subsegment of coordinates of a route
	 * @return - The ids of the street segments of the route
	 */
	Mono<List<Long>> fetchStepsFromChunk(List<OsmrMatchInformation> chunk) {
		String joinedCoords = chunk.stream()
			.map(coordinate -> String.format("%s,%s", coordinate.getLng(), coordinate.getLat()))
			.collect(Collectors.joining(";"));

		String joinedTimestamps = chunk.stream()
			.map(OsmrMatchInformation::getTimestamp)
			.map(String::valueOf)
			.collect(Collectors.joining(";"));

		String radius = chunk.stream()
			.map(OsmrMatchInformation::getAccuracy)
			.map(OsmrBackendService::calculateRadius)
			.collect(Collectors.joining(";"));

		Mono<OsmrMatchingResponse> responseMono = _webClient.get()
			.uri(uriBuilder -> uriBuilder.path("/" + joinedCoords)
				.queryParam("steps", true)
				.queryParam("radiuses", radius)
				.queryParam("timestamps", joinedTimestamps)
				.build())
			.retrieve()
			.bodyToMono(OsmrMatchingResponse.class);

		return responseMono.map(OsmrMatchingResponse::getMatchings)
			.flatMapMany(Flux::fromIterable)
			.collectList()
			.flatMapMany(matchings -> {
				if (matchings.size() >= 2) {
					return Flux.fromIterable(matchings).filter(matching -> matching.getLegs().size() >= 3);
				}
				else {
					return Flux.fromIterable(matchings);
				}
			})
			.flatMapIterable(OsmrMatching::getLegs)
			.flatMapIterable(OsmrLeg::getSteps)
			.filter(step -> step.getId() != null)
			.map(OsmrStep::getId)
			.collectList();
	}

	/**
	 * The method combines the chunked id lists of the street segments of the route to
	 * one.
	 * @param chunkedResponses - The list of id lists of the street segments of the route
	 * @return - The id of all unique street segments of the route
	 */
	List<Long> combineStepChunks(List<List<Long>> chunkedResponses) {
		return chunkedResponses.stream().flatMap(List::stream).distinct().toList();
	}

}
