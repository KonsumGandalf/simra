package com.simra.konsumgandalf.common.models.classes;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

/**
 * This class is used to store the information of the matched point from the OSRM service
 */
public class MatchInformation extends Coordinate implements Serializable {

	/**
	 * The timestamp of the matched point
	 */
	@JsonProperty("time")
	private long timestamp;

	public MatchInformation(double lng, double lat, long time) {
		super(lng, lat);
		this.timestamp = time;
	}

	public MatchInformation() {
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

}
