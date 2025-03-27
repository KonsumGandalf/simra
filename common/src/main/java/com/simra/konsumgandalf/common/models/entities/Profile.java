package com.simra.konsumgandalf.common.models.entities;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvCustomBindByName;
import com.simra.konsumgandalf.common.models.enums.AgeGroup;
import com.simra.konsumgandalf.common.models.enums.ExperienceGroup;
import com.simra.konsumgandalf.common.models.enums.Gender;
import com.simra.konsumgandalf.common.models.enums.SimraRegionGroup;
import com.simra.konsumgandalf.common.utils.converter.EnumConverter;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.persistence.Transient;

import java.util.Date;

@Entity
public class Profile {

	@Temporal(TemporalType.TIMESTAMP)
	private Date lastModified;

	@Id
	private String path;

	@CsvCustomBindByName(column = "birth", converter = EnumConverter.class)
	@Column(length = 21)
	@Enumerated(EnumType.STRING)
	private AgeGroup ageGroup = AgeGroup.NOT_CHOSEN;

	@CsvCustomBindByName(column = "gender", converter = EnumConverter.class)
	@Column(length = 10)
	@Enumerated(EnumType.STRING)
	private Gender gender = Gender.NOT_CHOSEN;

	@Transient
	@CsvCustomBindByName(column = "region", converter = EnumConverter.class)
	private SimraRegionGroup simraRegionGroup = SimraRegionGroup.NOT_CHOSEN;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "simra_region_name", referencedColumnName = "name")
	private SimraRegion simraRegion;

	@CsvCustomBindByName(column = "experience", converter = EnumConverter.class)
	@Column(length = 12)
	@Enumerated(EnumType.STRING)
	private ExperienceGroup experienceGroup = ExperienceGroup.NOT_CHOSEN;

	@CsvBindByName(column = "numberOfRides")
	@Column
	private Integer numberOfRides;

	@CsvBindByName(column = "duration")
	@Column
	private Long duration;

	@CsvBindByName(column = "numberOfIncidents")
	@Column
	private Integer numberOfIncidents = 0;

	@CsvBindByName(column = "numberOfScary")
	@Column
	private Integer numberOfScaryIncidents = 0;

	@CsvBindByName(column = "behaviour")
	@Column
	private Integer behaviour = -1;

	public Date getLastModified() {
		return lastModified;
	}

	public void setLastModified(Date lastModified) {
		this.lastModified = lastModified;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public AgeGroup getAgeGroup() {
		return ageGroup;
	}

	public void setAgeGroup(AgeGroup ageGroup) {
		this.ageGroup = ageGroup;
	}

	public Gender getGender() {
		return gender;
	}

	public void setGender(Gender gender) {
		this.gender = gender;
	}

	public SimraRegionGroup getSimraRegionGroup() {
		return simraRegionGroup;
	}

	public void setSimraRegionGroup(SimraRegionGroup simraRegionGroup) {
		this.simraRegionGroup = simraRegionGroup;
	}

	public SimraRegion getSimraRegion() {
		return simraRegion;
	}

	public void setSimraRegion(SimraRegion simraRegion) {
		this.simraRegion = simraRegion;
	}

	public ExperienceGroup getExperienceGroup() {
		return experienceGroup;
	}

	public void setExperienceGroup(ExperienceGroup experienceGroup) {
		this.experienceGroup = experienceGroup;
	}

	public Integer getNumberOfRides() {
		return numberOfRides;
	}

	public void setNumberOfRides(Integer numberOfRides) {
		this.numberOfRides = numberOfRides;
	}

	public Long getDuration() {
		return duration;
	}

	public void setDuration(Long duration) {
		this.duration = duration;
	}

	public Integer getNumberOfIncidents() {
		return numberOfIncidents;
	}

	public void setNumberOfIncidents(Integer numberOfIncidents) {
		this.numberOfIncidents = numberOfIncidents;
	}

	public Integer getNumberOfScaryIncidents() {
		return numberOfScaryIncidents;
	}

	public void setNumberOfScaryIncidents(Integer numberOfScaryIncidents) {
		this.numberOfScaryIncidents = numberOfScaryIncidents;
	}

	public Integer getBehaviour() {
		return behaviour;
	}

	public void setBehaviour(Integer behaviour) {
		this.behaviour = behaviour;
	}

}
