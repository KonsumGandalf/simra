package com.simra.konsumgandalf.common.models.entities;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import org.geolatte.geom.Geometry;

import java.util.ArrayList;
import java.util.List;

@Entity
public class RideCleanedLocation {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@Column(nullable = true)
	private Geometry way;

	@ManyToMany(mappedBy = "rideCleanedLocations",
			cascade = { CascadeType.DETACH, CascadeType.REFRESH, CascadeType.MERGE }, fetch = FetchType.EAGER)
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
