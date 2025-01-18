package com.simra.konsumgandalf.osmrBackend.services;

import com.simra.konsumgandalf.common.models.classes.Coordinate;
import com.simra.konsumgandalf.osmrBackend.models.OsmrNearestResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * This service is responsible for finding the nearest street segment to a given point.
 */
@Service
public class OsmrBackendNearestService extends OsmrBackendService {

	public OsmrBackendNearestService(@Value("${OSMR_BACKEND_URL}") String osmrBackendUrl) {
		super(osmrBackendUrl + "/nearest/v1/bike", 100);
	}

	/**
	 * Uses the OSMR backend to find the nearest street segment to a given point.
	 * @param coordinate The coordinate to find the nearest street segment to
	 * @return The ID of the nearest street segment
	 */
	public Long getIDNearestStreetToCoordinate(Coordinate coordinate) throws Exception {
		String joinedCoordinate = String.format("%s,%s", coordinate.getLng(), coordinate.getLat());
		OsmrNearestResponse response = webClient.get()
			.uri(uriBuilder -> uriBuilder.path("/" + joinedCoordinate).build())
			.retrieve()
			.bodyToMono(OsmrNearestResponse.class)
			.block();

		if (response == null || response.getWaypoints() == null || response.getWaypoints().isEmpty()) {
			throw new Exception("No suitable Street found");
		}

		return response.getWaypoints().getFirst().getId();
	}

}
