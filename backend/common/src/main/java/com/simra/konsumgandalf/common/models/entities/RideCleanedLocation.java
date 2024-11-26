package com.simra.konsumgandalf.common.models.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import jakarta.persistence.*;
import org.geolatte.geom.Geometry;
import org.n52.jackson.datatype.jts.GeometryDeserializer;
import org.n52.jackson.datatype.jts.GeometrySerializer;

import java.util.ArrayList;
import java.util.List;

@Entity
public class RideCleanedLocation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private Geometry way;

    private Geometry bufferedWay;

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

    public Geometry getBufferedWay() {
        return bufferedWay;
    }

    public void setBufferedWay(Geometry bufferedWay) {
        this.bufferedWay = bufferedWay;
    }

    public List<PlanetOsmLine> getPlanetOsmLines() {
        return planetOsmLines;
    }

    public void setPlanetOsmLines(List<PlanetOsmLine> planetOsmLines) {
        this.planetOsmLines = planetOsmLines;
    }
}
