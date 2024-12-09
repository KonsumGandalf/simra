package com.simra.konsumgandalf.osmrBackend.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * This class represents a waypoint in the OSMR routing response, which are the streets / PlanetOsmLines
 */
public class OsmrWaypoint {
    private String hint;
    private double distance;
    @JsonProperty("name")
    private Long id;
    private List<Double> location;

    // Getters and Setters
    public String getHint() {
        return hint;
    }

    public void setHint(String hint) {
        this.hint = hint;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<Double> getLocation() {
        return location;
    }

    public void setLocation(List<Double> location) {
        this.location = location;
    }
}
