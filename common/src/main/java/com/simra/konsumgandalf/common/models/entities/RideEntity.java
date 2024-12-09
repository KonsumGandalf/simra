package com.simra.konsumgandalf.common.models.entities;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is the root entity for all OSM objects.
 */
@Entity
public class RideEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "ride_entity_id", referencedColumnName = "id") // Add this annotation
    private List<RideLocation> rideLocation = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "ride_incident_id", referencedColumnName = "id") // Add this annotation
    private List<RideIncident> rideIncidents = new ArrayList<>();

    @OneToOne
    @JoinColumn(name = "ride_cleaned_incident", referencedColumnName = "id")
    private RideCleanedLocation rideCleanedLocation;

    @Column(unique = true)
    private String path;

    public RideEntity() {

    }


    public RideCleanedLocation getRideCleanedIncident() {
        return rideCleanedLocation;
    }

    public void setRideCleanedIncident(RideCleanedLocation rideCleanedLocation) {
        this.rideCleanedLocation = rideCleanedLocation;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<RideLocation> getRideLocation() {
        return rideLocation;
    }

    public void setRideLocation(List<RideLocation> rideLocation) {
        this.rideLocation = rideLocation;
    }

    public List<RideIncident> getRideIncidents() {
        return rideIncidents;
    }

    public void setRideIncidents(List<RideIncident> rideManualDescription) {
        this.rideIncidents = rideManualDescription;
    }

    public RideEntity(String path) {
        this.path = path;
    }

    public RideCleanedLocation getRideCleanedLocation() {
        return rideCleanedLocation;
    }

    public void setRideCleanedLocation(RideCleanedLocation rideCleanedLocation) {
        this.rideCleanedLocation = rideCleanedLocation;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
