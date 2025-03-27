package com.simra.konsumgandalf.common.models.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.simra.konsumgandalf.common.models.enums.SafetyMetricsProfileGroup;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import org.hibernate.annotations.NaturalId;

import java.util.ArrayList;
import java.util.List;

@Entity
public class SimraRegionGroupAssociation {

	@Id
	@GeneratedValue
	private Long id;

	String name;

	@ManyToOne
	@JoinColumn(name = "simra_region", referencedColumnName = "name")
	@JsonIgnore
	private SimraRegion simraRegion;

	@Enumerated(EnumType.STRING)
	private SafetyMetricsProfileGroup groupType;

	@OneToMany(mappedBy = "simraRegionGroupAssociation")
	@JsonManagedReference
	private List<SafetyMetricsProfile> groupValue = new ArrayList<>();

	public SimraRegionGroupAssociation() {
	}

	public SimraRegionGroupAssociation(SimraRegion simraRegion, SafetyMetricsProfileGroup group) {
		this.simraRegion = simraRegion;
		this.groupType = group;
		this.name = simraRegion.getName();
	}

	public SimraRegion getSimraRegion() {
		return simraRegion;
	}

	public void setSimraRegion(SimraRegion simraRegion) {
		this.simraRegion = simraRegion;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public SafetyMetricsProfileGroup getGroupType() {
		return groupType;
	}

	public void setGroupType(SafetyMetricsProfileGroup groupType) {
		this.groupType = groupType;
	}

	public List<SafetyMetricsProfile> getGroupValue() {
		return groupValue;
	}

	public void setGroupValue(List<SafetyMetricsProfile> group) {
		this.groupValue = group;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

}
