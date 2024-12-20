package com.simra.konsumgandalf.osmrBackend.models;

import java.util.List;

/**
 * This class represents a route in the OSMR routing response, which gives overall
 * information about the route.
 */
public class OsmrMatching {

	private double confidence;

	private String geometry;

	private List<OsmrLeg> legs;

	private String weight_name;

	private double weight;

	private double duration;

	private double distance;

	public String getGeometry() {
		return geometry;
	}

	public void setGeometry(String geometry) {
		this.geometry = geometry;
	}

	public List<OsmrLeg> getLegs() {
		return legs;
	}

	public void setLegs(List<OsmrLeg> legs) {
		this.legs = legs;
	}

	public String getWeight_name() {
		return weight_name;
	}

	public void setWeight_name(String weight_name) {
		this.weight_name = weight_name;
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

	public double getConfidence() {
		return confidence;
	}

	public void setConfidence(double confidence) {
		this.confidence = confidence;
	}

}
