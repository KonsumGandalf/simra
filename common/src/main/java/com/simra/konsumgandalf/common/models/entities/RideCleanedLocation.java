package com.simra.konsumgandalf.common.models.entities;

import jakarta.persistence.*;
import org.geolatte.geom.Geometry;

import java.util.ArrayList;
import java.util.List;

@Entity
public class RideCleanedLocation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private Geometry way;

    @ManyToMany(
            mappedBy = "rideCleanedLocations",
            cascade = {CascadeType.DETACH, CascadeType.REFRESH, CascadeType.MERGE},
            fetch = FetchType.EAGER
    )
    private List<PlanetOsmLine> planetOsmLines = new ArrayList<>();

    public RideCleanedLocation() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Geometry getWay() {
        return way;
    }

    public void setWay(Geometry way) {
        this.way = way;
    }

    public List<PlanetOsmLine> getPlanetOsmLines() {
        return planetOsmLines;
    }

    public void setPlanetOsmLines(List<PlanetOsmLine> planetOsmLines) {
        this.planetOsmLines = planetOsmLines;
    }
}
