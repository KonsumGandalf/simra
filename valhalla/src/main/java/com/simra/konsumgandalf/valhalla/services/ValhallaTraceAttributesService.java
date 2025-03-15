package com.simra.konsumgandalf.valhalla.services;

import com.google.common.collect.Lists;
import com.simra.konsumgandalf.common.models.classes.OsmrMatchInformation;
import com.simra.konsumgandalf.valhalla.models.ValhallaEdge;
import com.simra.konsumgandalf.valhalla.models.ValhallaTraceAttributesResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This service is dedicated to deal with the endpoint /trace_attributes, which is used to
 * match street segments
 */
@Service
public class ValhallaTraceAttributesService extends ValhallaService {

	private static final Map<String, Object> BASE_PAYLOAD = Map.of("costing", "bicycle", "shape_match", "map_snap",
			"snap_prevention", List.of("motorway", "trunk", "primary"), "filters",
			Map.of("attributes", List.of("edge.way_id"), "action", "include"));

	private final int TURN_PENALTY_FACTOR;

	public ValhallaTraceAttributesService(@Value("${VALHALLA_BACKEND_URL}") String osmrBackendUrl,
			@Value("${VALHALLA_TURN_PENALTY_FACTOR}") int turnPenaltyFactor) {
		super(osmrBackendUrl + "/trace_attributes", 500, 10 * 1024 * 1024);
		TURN_PENALTY_FACTOR = turnPenaltyFactor;
	}

	public List<Long> calculateStreetSegmentIdsOfRoute(List<OsmrMatchInformation> coordinates) {
		ArrayList<OsmrMatchInformation> filteredTimestampList = new ArrayList<>();

		long lastTimestamp = -1;

		for (OsmrMatchInformation coordinate : coordinates) {
			// 3 seconds is the minimum time difference between two coordinates
			if (lastTimestamp == -1 || coordinate.getTimestamp() - lastTimestamp > 3) {
				lastTimestamp = coordinate.getTimestamp();
				filteredTimestampList.add(coordinate);
			}
		}

		List<List<OsmrMatchInformation>> partitions = Lists.partition(filteredTimestampList, DEFAULT_PARTITION_SIZE);
		return Flux.fromIterable(partitions)
			.flatMap(this::fetchWithRetry)
			.collectList()
			.map(this::combineChunks)
			.onErrorResume(ex -> {
				logger.warn("Error during calculation of street segments: {}", ex.getMessage());
				return Mono.just(new ArrayList<>());
			})
			.block();
	}

	public Mono<List<Long>> fetchWithRetry(List<OsmrMatchInformation> chunk) {
		return fetchIdsFromChunk(chunk).onErrorResume(WebClientResponseException.class, ex -> {
			if (isNotFoundStreetSegmentError(ex)) {
				return retryWithSmallerPartitions(chunk);
			}

			logger.error("Error fetching steps for chunk: {} - HTTP Status: {}", chunk, ex.getStatusCode());
			return Mono.just(new ArrayList<>());
		});
	}

	private Mono<List<Long>> retryWithSmallerPartitions(List<OsmrMatchInformation> chunk) {
		List<List<OsmrMatchInformation>> subPartitions = Lists.partition(chunk, chunk.size() / 2);
		return Flux.fromIterable(subPartitions)
			.filter(subPartition -> subPartition.size() > 4)
			.flatMap(subPartition -> fetchIdsFromChunk(subPartition).onErrorResume(WebClientResponseException.class,
					ex -> {
						if (isNotFoundStreetSegmentError(ex)) {
							return retryWithSmallerPartitions(subPartition);
						}
						return Mono.just(new ArrayList<>());
					}))
			.delayElements(Duration.ofMillis(200))
			.collectList()
			.map(this::combineChunks);
	}

	public Mono<List<Long>> fetchIdsFromChunk(List<OsmrMatchInformation> coordinates) {
		Map<String, Object> payload = new HashMap<>(BASE_PAYLOAD);
		payload.put("shape", coordinates);
		payload.put("trace_options", Map.of("turn_penalty_factor", TURN_PENALTY_FACTOR));

		return webClient.post()
			.bodyValue(payload)
			.retrieve()
			.bodyToMono(ValhallaTraceAttributesResponse.class)
			.flatMapMany(response -> Flux.fromIterable(response.getEdges()))
			.map(ValhallaEdge::getId)
			.distinct()
			.collectList();
	}

	/**
	 * The method combines the chunked id lists of the street segments of the route to
	 * one.
	 * @param chunkedResponses - The list of id lists of the street segments of the route
	 * @return - The id of all unique street segments of the route
	 */
	List<Long> combineChunks(List<List<Long>> chunkedResponses) {
		return chunkedResponses.stream().flatMap(List::stream).distinct().toList();
	}

	boolean isNotFoundStreetSegmentError(WebClientResponseException ex) {
		return ex.getStatusCode() == HttpStatus.BAD_REQUEST && (ex.getResponseBodyAsString()
			.contains(
					"Map Match algorithm failed to find path: map_snap algorithm failed to snap the shape points to the correct shape.")
				|| ex.getResponseBodyAsString().contains("No suitable edges near location"));
	}

}
