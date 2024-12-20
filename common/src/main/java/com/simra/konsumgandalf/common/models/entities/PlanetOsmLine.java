package com.simra.konsumgandalf.common.models.entities;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import java.util.ArrayList;
import java.util.List;
import org.geolatte.geom.Geometry;

@Entity()
public class PlanetOsmLine {

	@Column(name = "osm_id")
	private long osm_id;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id; // New primary key field

	private Geometry way;

	@ManyToMany(cascade = { CascadeType.DETACH, CascadeType.REFRESH, CascadeType.MERGE }, fetch = FetchType.EAGER)
	@JoinTable(name = "ride_cleaned_location__planet_osm_line", joinColumns = @JoinColumn(name = "planet_osm_line_id"),
			inverseJoinColumns = @JoinColumn(name = "ride_cleaned_location_id"))
	private List<RideCleanedLocation> rideCleanedLocations = new ArrayList<>();

	public PlanetOsmLine() {
	}

	public PlanetOsmLine(long id, Geometry way) {
		this.id = id;
		this.way = way;
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

	public List<RideCleanedLocation> getRideCleanedLocations() {
		return rideCleanedLocations;
	}

	public void setRideCleanedLocations(List<RideCleanedLocation> rideCleanedLocations) {
		this.rideCleanedLocations = rideCleanedLocations;
	}

}
