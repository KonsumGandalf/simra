package com.simra.konsumgandalf.osmrBackend.models;

import java.util.List;

/**
 * This class represents a leg in the OSMR routing response, which are the segments of the route.
 */
public class OsmrLeg {
    private List<OsmrStep> steps;
    private String summary;
    private double weight;
    private double duration;
    private double distance;

    public List<OsmrStep> getSteps() {
        return steps;
    }

    public void setSteps(List<OsmrStep> steps) {
        this.steps = steps;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
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
}
