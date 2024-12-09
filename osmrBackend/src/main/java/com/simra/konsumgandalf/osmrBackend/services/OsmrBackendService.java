package com.simra.konsumgandalf.osmrBackend.services;

import com.google.common.collect.Lists;
import com.simra.konsumgandalf.common.models.classes.Coordinate;
import com.simra.konsumgandalf.osmrBackend.models.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.stream.Collectors;

/**
 * This service interacts with the OSMR backend.
 */
@Service
public class OsmrBackendService {
    private final WebClient _webClient;

    public OsmrBackendService(@Value("${OSMR_BACKEND_MATCH_URL}") String osmrBackendUrl) {
        _webClient = WebClient.builder().baseUrl(osmrBackendUrl).build();
    }

    /**
     * This method recognizes the street segments for a list of coordinates.
     * The method partitions the list of coordinates into chunks of 100 coordinates each since osmr-backend sets a default limit of 100 coordinates.
     * The partitions are then processed concurrently.
     *
     * @param coordinates - The list of coordinates of a route
     * @return - The id of the street segments of the route
     */
    public List<Long> calculateStreetSegmentOsmIdsOfRoute(List<Coordinate> coordinates) {
        List<List<Coordinate>> partitions = Lists.partition(coordinates, 100);

        return Flux.fromIterable(partitions)
                .flatMap(this::fetchStepsFromChunk)
                .collectList()
                .map(this::combineStepChunks)
                .block();
    }

    /**
     * This method fetches from the OSMR backend and returns the ids of the steps (street segments) of the route.
     *
     * @param chunk - The subsegment of coordinates of a route
     * @return - The id of the street segments of the route
     */
    Mono<List<Long>> fetchStepsFromChunk(List<Coordinate> chunk) {
        String joinedCoords = chunk.stream()
                .map(coordinate -> String.format("%s,%s", coordinate.getLng(), coordinate.getLat()))
                .collect(Collectors.joining(";"));

        Mono<OsmrMatchingResponse> responseMono = _webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/" + joinedCoords)
                        .queryParam("steps", true)
                        .build())
                .retrieve()
                .bodyToMono(OsmrMatchingResponse.class);

        return responseMono.map(OsmrMatchingResponse::getMatchings)
                .flatMapMany(Flux::fromIterable)
                .sort(Comparator.comparingDouble(OsmrMatching::getConfidence).reversed())
                .next()
                .flatMapIterable(OsmrMatching::getLegs)
                .flatMapIterable(OsmrLeg::getSteps)
                .filter(step -> step.getId() != null)
                .map(OsmrStep::getId)
                .collectList();
    }

    /**
     * The method combines the chunked id lists of the street segments of the route to one.
     *
     * @param chunkedResponses - The list of id lists of the street segments of the route
     * @return - The id of all unique street segments of the route
     */
     List<Long> combineStepChunks(List<List<Long>> chunkedResponses) {
        return chunkedResponses.stream()
                .flatMap(List::stream)
                .distinct()
                .toList();
    }

}
