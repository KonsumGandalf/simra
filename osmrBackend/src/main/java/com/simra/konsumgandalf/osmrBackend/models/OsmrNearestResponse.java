package com.simra.konsumgandalf.osmrBackend.models;

import java.util.List;

public class OsmrNearestResponse {

	private List<OsmrWaypoint> waypoints;

	public List<OsmrWaypoint> getWaypoints() {
		return waypoints;
	}

	public void setWaypoints(List<OsmrWaypoint> waypoints) {
		this.waypoints = waypoints;
	}

}
