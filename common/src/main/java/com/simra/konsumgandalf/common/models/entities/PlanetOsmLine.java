package com.simra.konsumgandalf.common.models.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import org.geolatte.geom.Geometry;

import java.util.List;
import java.util.Set;

@Entity()
@Table(indexes = { @Index(columnList = "osm_id") })
public class PlanetOsmLine {

	@Id
	@GeneratedValue
	@Column(name = "osm_id", unique = true)
	private long id;

	/**
	 * The metrics indicating the safety of this street segment. Therefore, the
	 * {@link #rideIncident} field is used to calculate the metrics.
	 */
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "planetOsmLine", orphanRemoval = true)
	private List<SafetyMetrics> safetyMetrics;

	/**
	 * The incidents that occurred on this street
	 */
	@OneToMany(cascade = { CascadeType.PERSIST, CascadeType.REMOVE }, orphanRemoval = true, fetch = FetchType.LAZY)
	@JoinColumn(name = "planet_osm_line_osm_id")
	private List<RideIncident> rideIncident;

	@Column
	private Geometry way;

	@Column
	private String highway;

	@ManyToMany(cascade = { CascadeType.DETACH, CascadeType.REFRESH, CascadeType.MERGE, CascadeType.PERSIST },
			fetch = FetchType.LAZY, mappedBy = "planetOsmLines")
	private Set<RideEntity> rideEntities;

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

	public List<SafetyMetrics> getSafetyMetrics() {
		return safetyMetrics;
	}

	public void setSafetyMetrics(List<SafetyMetrics> safetyMetrics) {
		this.safetyMetrics = safetyMetrics;
	}

	public Set<RideEntity> getRideEntities() {
		return rideEntities;
	}

	public void setRideEntities(Set<RideEntity> rideEntities) {
		this.rideEntities = rideEntities;
	}

}
