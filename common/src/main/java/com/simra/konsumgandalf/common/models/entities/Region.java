package com.simra.konsumgandalf.common.models.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;

import java.util.List;
import java.util.Objects;

/**
 * Represents an administrative region like a state or a city.
 */
@Entity
public class Region {
	@Id
	@Column
	private String name;

	@Column
	private Long osmId;

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "region")
	@JsonBackReference
	private List<SafetyMetricsRegion> safetyMetricsRegions;

	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "simra_region_name")
	@JsonBackReference
	private SimraRegion simraRegion;

	public Region() {
	}

	public Region(String name, Long osmId) {
		this.name = name;
		this.osmId = osmId;
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

	public void setSafetyMetricsRegions(
			List<SafetyMetricsRegion> safetyMetricsCities) {
		this.safetyMetricsRegions = safetyMetricsCities;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Region region = (Region) o;
		return Objects.equals(name, region.name);
	}

	@Override
	public int hashCode() {
		return Objects.hash(name);
	}

	public SimraRegion getSimraRegion() {
		return simraRegion;
	}

	public void setSimraRegion(SimraRegion simraRegion) {
		this.simraRegion = simraRegion;
	}

	public Long getOsmId() {
		return osmId;
	}

	public void setOsmId(Long osmId) {
		this.osmId = osmId;
	}
}
