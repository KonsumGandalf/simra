package com.simra.konsumgandalf.osmrBackend.models;

import java.util.List;

/**
 * This class represents a response from the OSMR backend.
 */
public class OsmrMatchingResponse {

	private List<OsmrMatching> matchings;

	private List<OsmrWaypoint> waypoints;

	public List<OsmrWaypoint> getWaypoints() {
		return waypoints;
	}

	public void setWaypoints(List<OsmrWaypoint> waypoints) {
		this.waypoints = waypoints;
	}

	public List<OsmrMatching> getMatchings() {
		return matchings;
	}

	public void setMatchings(List<OsmrMatching> matchings) {
		this.matchings = matchings;
	}

}
