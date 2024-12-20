package com.simra.konsumgandalf.osmrBackend.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public class OsmrStep {

	private String geometry;

	// private Maneuver maneuver;
	private String mode;

	private String driving_side;

	@JsonProperty("name")
	private Long id;

	// private ArrayList<Intersection> intersections;
	private double weight;

	private double duration;

	private double distance;

	private String ref;

	private String destinations;

	public String getGeometry() {
		return geometry;
	}

	public void setGeometry(String geometry) {
		this.geometry = geometry;
	}

	public String getMode() {
		return mode;
	}

	public void setMode(String mode) {
		this.mode = mode;
	}

	public String getDriving_side() {
		return driving_side;
	}

	public void setDriving_side(String driving_side) {
		this.driving_side = driving_side;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public double getWeight() {
		return weight;
	}

	public void setWeight(double weight) {
		this.weight = weight;
	}

	public double getDuration() {
		return duration;
	}

	public void setDuration(double duration) {
		this.duration = duration;
	}

	public double getDistance() {
		return distance;
	}

	public void setDistance(double distance) {
		this.distance = distance;
	}

	public String getRef() {
		return ref;
	}

	public void setRef(String ref) {
		this.ref = ref;
	}

	public String getDestinations() {
		return destinations;
	}

	public void setDestinations(String destinations) {
		this.destinations = destinations;
	}

}
