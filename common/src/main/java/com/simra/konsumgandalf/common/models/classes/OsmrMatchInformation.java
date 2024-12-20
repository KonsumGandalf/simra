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

	/**
	 * The accuracy of the matched point
	 */
	private double accuracy;

	public OsmrMatchInformation(double lng, double lat, long time, double accuracy) {
		super(lng, lat);
		this.timestamp = time;
		this.accuracy = accuracy;
	}

	public OsmrMatchInformation() {
	}

	public double getAccuracy() {
		return accuracy;
	}

	public void setAccuracy(double accuracy) {
		this.accuracy = accuracy;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

}
