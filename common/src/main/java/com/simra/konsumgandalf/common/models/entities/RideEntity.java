package com.simra.konsumgandalf.common.models.entities;

import com.simra.konsumgandalf.common.models.classes.RideLocation;
import com.simra.konsumgandalf.common.models.enums.TrafficTimes;
import com.simra.konsumgandalf.common.models.enums.WeekDays;
import com.simra.konsumgandalf.common.models.maps.TrafficTimesMapper;
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
import jakarta.persistence.PrePersist;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.persistence.Transient;
import org.geolatte.geom.Geometry;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * This class is the root entity for all OSM objects.
 */
@Entity
public class RideEntity extends TimeBaseClass {

	private static final Calendar calendar = new GregorianCalendar();

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Temporal(TemporalType.TIMESTAMP)
	private Date rideStart;

	@Temporal(TemporalType.TIMESTAMP)
	private Date rideEnd;

	@Column(nullable = true)
	private Geometry way;

	@Transient
	private List<RideLocation> rideLocations = new ArrayList<>();

	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinColumn(name = "ride_entity_id", referencedColumnName = "id")
	private List<RideIncident> rideIncidents = new ArrayList<>();

	@ManyToMany(cascade = { CascadeType.DETACH, CascadeType.REFRESH, CascadeType.MERGE }, fetch = FetchType.EAGER)
	@JoinTable(name = "ride_entity__planet_osm_line")
	private List<PlanetOsmLine> planetOsmLines = new ArrayList<>();

	@Column(columnDefinition = "text")
	private String coordinates;

	@Column(unique = true)
	private String path;

	public RideEntity() {

	}

	public RideEntity(String path) {
		this.path = path;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public List<RideIncident> getRideIncidents() {
		return rideIncidents;
	}

	public void setRideIncidents(List<RideIncident> rideManualDescription) {
		this.rideIncidents = rideManualDescription;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public Date getRideStart() {
		return rideStart;
	}

	public void setRideStart(Date rideStart) {
		this.rideStart = rideStart;
	}

	public Date getRideEnd() {
		return rideEnd;
	}

	public void setRideEnd(Date rideEnd) {
		this.rideEnd = rideEnd;
	}

	public List<PlanetOsmLine> getPlanetOsmLines() {
		return planetOsmLines;
	}

	public void setPlanetOsmLines(List<PlanetOsmLine> planetOsmLines) {
		this.planetOsmLines = planetOsmLines;
	}

	public String getCoordinates() {
		return coordinates;
	}

	public void setCoordinates(String coordinates) {
		this.coordinates = coordinates;
	}

	public List<RideLocation> getRideLocations() {
		return rideLocations;
	}

	public void setRideLocations(List<RideLocation> rideLocation) {
		this.rideLocations = rideLocation;
	}

	public Geometry getWay() {
		return way;
	}

	public void setWay(Geometry way) {
		this.way = way;
	}

	@PrePersist
	private void calculateTrafficTimesAndWeekDays() {
		if (rideStart == null || rideEnd == null) {
			return;
		}

		Date rideMedianDate = new Date((rideStart.getTime() + rideEnd.getTime()) / 2);
		TrafficTimes trafficTime = TrafficTimesMapper.getTrafficTime(rideMedianDate);
		calendar.setTime(rideMedianDate);

		int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1;

		WeekDays weekDay = dayOfWeek <= 5 ? WeekDays.WEEK : WeekDays.WEEKEND;
		int year = calendar.get(Calendar.YEAR);

		super.setYear(year);
		super.setWeekDay(weekDay);
		super.setTrafficTime(trafficTime);

		for (RideIncident rideIncident : rideIncidents) {
			rideIncident.setTrafficTime(trafficTime);
			rideIncident.setWeekDay(weekDay);
			rideIncident.setYear(year);
		}
	}

}
