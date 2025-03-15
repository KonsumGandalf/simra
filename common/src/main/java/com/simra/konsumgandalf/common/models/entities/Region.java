package com.simra.konsumgandalf.common.models.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import org.locationtech.jts.geom.Geometry;

import java.util.List;
import java.util.Objects;

/**
 * Represents an administrative region like a state or a city.
 */
@Entity
public class Region {

	@Id
	@Column(unique = true)
	private String name;

	@Column
	private Long id;

	@Column
	private int adminLevel;

	/**
	 * The average length of all street segments in this region in meters.
	 */
	@Column
	private Float avgSegmentDistance;

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "region", fetch = FetchType.LAZY)
	@JsonIgnore
	private List<SafetyMetricsRegion> safetyMetricsRegions;

	@ManyToMany(cascade = CascadeType.ALL)
	@JsonBackReference
	private List<SimraRegion> simraRegions;

	@Column()
	private Geometry way;

	public Region() {
	}

	public Region(String name, Long id, int adminLevel) {
		this.name = name;
		this.id = id;
		this.adminLevel = adminLevel;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<SafetyMetricsRegion> getSafetyMetricsRegions() {
		return safetyMetricsRegions;
	}

	public void setSafetyMetricsRegions(List<SafetyMetricsRegion> safetyMetricsCities) {
		this.safetyMetricsRegions = safetyMetricsCities;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		Region region = (Region) o;
		return Objects.equals(name, region.name);
	}

	@Override
	public int hashCode() {
		return Objects.hash(name);
	}

	public List<SimraRegion> getSimraRegions() {
		return simraRegions;
	}

	public void setSimraRegions(List<SimraRegion> simraRegions) {
		this.simraRegions = this.simraRegions;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long osmId) {
		this.id = osmId;
	}

	public Float getAvgSegmentDistance() {
		return avgSegmentDistance;
	}

	public void setAvgSegmentDistance(Float avgSegmentDistance) {
		this.avgSegmentDistance = avgSegmentDistance;
	}

	public Geometry getWay() {
		return way;
	}

	public void setWay(Geometry way) {
		this.way = way;
	}

	public int getAdminLevel() {
		return adminLevel;
	}

	public void setAdminLevel(int level) {
		this.adminLevel = level;
	}

}
