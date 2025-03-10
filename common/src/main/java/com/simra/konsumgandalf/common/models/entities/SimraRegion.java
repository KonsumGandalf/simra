package com.simra.konsumgandalf.common.models.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;

import java.util.List;

/**
 * Contains multiple regions to a super region.
 */
@Entity
public class SimraRegion {
	@Id
	@Column
	private String name;

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "region")
	@JsonBackReference
	private List<SafetyMetricsSimraRegion> safetyMetricsSimraRegions;

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "simraRegion")
	@JsonManagedReference
	private List<Region> regions;

	public SimraRegion() {
	}

	public SimraRegion(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<Region> getRegions() {
		return regions;
	}

	public void setRegions(
			List<Region> regions) {
		this.regions = regions;
	}

	public List<SafetyMetricsSimraRegion> getSafetyMetricsSimraRegions() {
		return safetyMetricsSimraRegions;
	}

	public void setSafetyMetricsSimraRegions(
			List<SafetyMetricsSimraRegion> safetyMetricsSimraRegions) {
		this.safetyMetricsSimraRegions = safetyMetricsSimraRegions;
	}
}
