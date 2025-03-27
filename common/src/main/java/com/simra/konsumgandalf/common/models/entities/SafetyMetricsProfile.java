package com.simra.konsumgandalf.common.models.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.simra.konsumgandalf.common.models.enums.SafetyMetricsProfileGroup;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinColumns;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import org.hibernate.annotations.NaturalId;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.Date;

@Entity
@EntityListeners(AuditingEntityListener.class)
public class SafetyMetricsProfile {

	@Id
	@GeneratedValue
	private Long id;

	@NaturalId
	private String groupName;

	@Column
	private int totalIncidents;

	@Column
	private int totalScaryIncidents;

	@Column
	private int totalRides;

	@Column
	private float dangerousScore;

	@NaturalId
	private String regionName;

	@NaturalId
	@Enumerated(EnumType.STRING)
	private SafetyMetricsProfileGroup groupType;

	@ManyToOne
	@JsonBackReference
	private SimraRegionGroupAssociation simraRegionGroupAssociation;

	public SafetyMetricsProfile() {
	}

	public SafetyMetricsProfile(String groupName, int totalRides, int totalIncidents, int totalScaryIncidents) {
		this.totalIncidents = totalIncidents;
		this.totalRides = totalRides;
		this.groupName = groupName;
		this.totalScaryIncidents = totalScaryIncidents;
	}

	public int getTotalIncidents() {
		return totalIncidents;
	}

	public void setTotalIncidents(int totalIncidents) {
		this.totalIncidents = totalIncidents;
	}

	public int getTotalRides() {
		return totalRides;
	}

	public void setTotalRides(int totalRides) {
		this.totalRides = totalRides;
	}

	public float getDangerousScore() {
		return dangerousScore;
	}

	public void setDangerousScore(float dangerousScore) {
		this.dangerousScore = dangerousScore;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String group) {
		this.groupName = group;
	}

	public SimraRegionGroupAssociation getSimraRegionGroupAssociation() {
		return simraRegionGroupAssociation;
	}

	public void setSimraRegionGroupAssociation(SimraRegionGroupAssociation simraRegionGroupAssociation) {
		this.simraRegionGroupAssociation = simraRegionGroupAssociation;
	}

	public int getTotalScaryIncidents() {
		return totalScaryIncidents;
	}

	public void setTotalScaryIncidents(int totalScaryIncidents) {
		this.totalScaryIncidents = totalScaryIncidents;
	}

	public SafetyMetricsProfileGroup getGroupType() {
		return groupType;
	}

	public void setGroupType(SafetyMetricsProfileGroup groupType) {
		this.groupType = groupType;
	}

	public String getRegionName() {
		return regionName;
	}

	public void setRegionName(String regionName) {
		this.regionName = regionName;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

}
