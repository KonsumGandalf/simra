package com.simra.konsumgandalf.common.models.classes;

import java.io.Serializable;

public class Coordinate implements Serializable {

	private double lng;

	private double lat;

	public Coordinate(double lng, double lat) {
		this.lng = lng;
		this.lat = lat;
	}

	public Coordinate() {
	}

	public double getLng() {
		return this.lng;
	}

	public void setLng(double lng) {
		this.lng = lng;
	}

	public double getLat() {
		return this.lat;
	}

	public void setLat(double lat) {
		this.lat = lat;
	}

}
