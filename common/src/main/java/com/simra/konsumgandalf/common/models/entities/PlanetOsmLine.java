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
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import org.geolatte.geom.Geometry;

import java.util.ArrayList;
import java.util.List;

@Entity()
public class PlanetOsmLine {

	@Column(name = "osm_id")
	private long osm_id;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id; // New primary key field

	/**
	 * The metrics indicating the safety of this street segment. Therefore, the
	 * {@link #rideIncident} field is used to calculate the metrics.
	 */
	@OneToOne(cascade = CascadeType.PERSIST)
	@JoinColumn(name = "safety_metrics_id")
	private SafetyMetrics safetyMetrics;

	/**
	 * The incidents that occurred on this street
	 */
	@OneToMany(cascade = { CascadeType.PERSIST, CascadeType.REMOVE }, orphanRemoval = true)
	@JoinColumn(name = "planet_osm_line_id")
	private List<RideIncident> rideIncident;

	@Column
	private Geometry way;

	@Column
	private String highway;

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

	public String getHighway() {
		return highway;
	}

	public void setHighway(String highway) {
		this.highway = highway;
	}

	public List<RideIncident> getRideIncident() {
		return rideIncident;
	}

	public void setRideIncident(List<RideIncident> rideIncident) {
		this.rideIncident = rideIncident;
	}

	public void setRideIncident(RideIncident rideIncident) {
		this.getRideIncident().add(rideIncident);
	}

	public SafetyMetrics getSafetyMetrics() {
		return safetyMetrics;
	}

	public void setSafetyMetrics(SafetyMetrics safetyMetrics) {
		this.safetyMetrics = safetyMetrics;
	}

}
