package com.simra.konsumgandalf.common.models.classes;

import java.io.Serializable;

/**
 * This class is used to store the information of the matched point from the OSRM service
 */
public class OsmrMatchInformation extends Coordinate implements Serializable {

	/**
	 * The timestamp of the matched point
	 */
	private long timestamp;

	public OsmrMatchInformation(double lng, double lat, long time) {
		super(lng, lat);
		this.timestamp = time;
	}

	public OsmrMatchInformation() {
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

}
