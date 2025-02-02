package com.simra.konsumgandalf.common.models.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvCustomBindByName;
import com.simra.konsumgandalf.common.models.enums.BikeType;
import com.simra.konsumgandalf.common.models.enums.IncidentType;
import com.simra.konsumgandalf.common.models.enums.ParticipantType;
import com.simra.konsumgandalf.common.models.enums.PhoneLocation;
import com.simra.konsumgandalf.common.models.enums.TrafficTimes;
import com.simra.konsumgandalf.common.models.enums.WeekDays;
import com.simra.konsumgandalf.common.models.maps.TrafficTimesMapper;
import com.simra.konsumgandalf.common.utils.converter.EnumConverter;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.persistence.Transient;
import org.hibernate.annotations.Formula;

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(indexes = { @Index(columnList = "planet_osm_line_osm_id") })
@JsonIgnoreProperties({ "i1", "i2", "i3", "i4", "i5", "i6", "i7", "i8", "i9", "i10", "ts" })
public class RideIncident extends TimeBaseClass {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "planet_osm_line_osm_id")
	@JsonIgnore
	private PlanetOsmLine planetOsmLine;

	@ManyToOne(fetch = FetchType.LAZY)
	@JsonIgnore
	private RideEntity rideEntity;

	@CsvBindByName(column = "lat")
	private double lat;

	@CsvBindByName(column = "lon")
	private double lng;

	@CsvCustomBindByName(column = "bike", converter = EnumConverter.class)
	@Column(length = 20)
	@Enumerated(EnumType.STRING)
	private BikeType bike;

	@CsvBindByName(column = "childCheckBox")
	private boolean childCheckBox;

	@CsvBindByName(column = "trailerCheckBox")
	private boolean trailerCheckBox;

	@CsvCustomBindByName(column = "pLoc", converter = EnumConverter.class)
	@Column(length = 16)
	@Enumerated(EnumType.STRING)
	private PhoneLocation phoneLocation = PhoneLocation.OTHER;

	@CsvCustomBindByName(column = "incident", converter = EnumConverter.class)
	@Column(length = 20)
	@Enumerated(EnumType.STRING)
	private IncidentType incidentType = IncidentType.OTHER;

	@CsvBindByName(column = "desc")
	@Column(columnDefinition = "text")
	private String description;

	@CsvBindByName(column = "scary")
	private boolean scary;

	@ElementCollection(fetch = FetchType.EAGER)
	@Enumerated(EnumType.STRING)
	@Column(length = 16)
	private List<ParticipantType> participantsInvolved = new ArrayList<>();

	@CsvBindByName(column = "ts")
	@Transient
	private long ts;

	@Temporal(TemporalType.TIMESTAMP)
	private java.util.Date timeStamp;

	public RideIncident() {
	}

	public RideIncident(double lat, double lng, long ts, BikeType bike, boolean childCheckBox, boolean trailerCheckBox,
			PhoneLocation phoneLocation, IncidentType incidentType, String description, boolean scary, long timeStamp) {
		this.lat = lat;
		this.lng = lng;
		this.ts = ts;
		this.bike = bike;
		this.childCheckBox = childCheckBox;
		this.trailerCheckBox = trailerCheckBox;
		this.phoneLocation = phoneLocation;
		this.incidentType = incidentType;
		this.description = description;
		this.scary = scary;
		this.ts = timeStamp;
	}

	@PrePersist
	private void calculateTrafficTimesAndWeekDays() {
		if (timeStamp == null) {
			return;
		}

		TrafficTimes trafficTime = TrafficTimesMapper.getTrafficTime(timeStamp);

		int dayOfWeek = timeStamp.getDay();

		WeekDays weekDay = dayOfWeek <= 5 ? WeekDays.WEEK : WeekDays.WEEKEND;

		super.setWeekDay(weekDay);
		super.setTrafficTime(trafficTime);
	}

	/**
	 * The following attributes pollute the entity with unnecessary information therefore
	 * they are not saved
	 * @return
	 */

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public RideEntity getRideEntity() {
		return rideEntity;
	}

	public void setRideEntity(RideEntity rideEntity) {
		this.rideEntity = rideEntity;
	}

	public double getLat() {
		return lat;
	}

	public void setLat(double lat) {
		this.lat = lat;
	}

	public double getLng() {
		return lng;
	}

	public void setLng(double lon) {
		this.lng = lon;
	}

	public BikeType getBike() {
		return bike;
	}

	public void setBike(BikeType bike) {
		this.bike = bike;
	}

	public boolean getChildCheckBox() {
		return childCheckBox;
	}

	public void setChildCheckBox(boolean childCheckBox) {
		this.childCheckBox = childCheckBox;
	}

	public boolean getTrailerCheckBox() {
		return trailerCheckBox;
	}

	public void setTrailerCheckBox(boolean trailerCheckBox) {
		this.trailerCheckBox = trailerCheckBox;
	}

	public PhoneLocation getPhoneLocation() {
		return phoneLocation;
	}

	public void setPhoneLocation(PhoneLocation phoneLocation) {
		this.phoneLocation = phoneLocation;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String desc) {
		this.description = desc;
	}

	public PlanetOsmLine getPlanetOsmLine() {
		return planetOsmLine;
	}

	public void setPlanetOsmLine(PlanetOsmLine planetOsmLine) {
		this.planetOsmLine = planetOsmLine;
	}

	public IncidentType getIncidentType() {
		return incidentType;
	}

	public void setIncidentType(IncidentType incidentType) {
		this.incidentType = incidentType;
	}

	public List<ParticipantType> getParticipantsInvolved() {
		return participantsInvolved;
	}

	public void setParticipantsInvolved(List<ParticipantType> participantsInvolved) {
		this.participantsInvolved = participantsInvolved;
	}

	public void addParticipantsInvolved(ParticipantType participantInvolved) {
		this.participantsInvolved.add(participantInvolved);
	}

	@Transient
	@CsvBindByName(column = "i1")
	private int i1;

	@Transient
	@CsvBindByName(column = "i2")
	private int i2;

	@Transient
	@CsvBindByName(column = "i3")
	private int i3;

	@Transient
	@CsvBindByName(column = "i4")
	private int i4;

	@Transient
	@CsvBindByName(column = "i5")
	private int i5;

	@Transient
	@CsvBindByName(column = "i6")
	private int i6;

	@Transient
	@CsvBindByName(column = "i7")
	private int i7;

	@Transient
	@CsvBindByName(column = "i8")
	private int i8;

	@Transient
	@CsvBindByName(column = "i9")
	private int i9;

	@Transient
	@CsvBindByName(column = "i10")
	private int i10;

	public int getI1() {
		return i1;
	}

	public void setI1(int i1) {
		this.i1 = i1;
	}

	public int getI2() {
		return i2;
	}

	public void setI2(int i2) {
		this.i2 = i2;
	}

	public int getI3() {
		return i3;
	}

	public void setI3(int i3) {
		this.i3 = i3;
	}

	public int getI4() {
		return i4;
	}

	public void setI4(int i4) {
		this.i4 = i4;
	}

	public int getI5() {
		return i5;
	}

	public void setI5(int i5) {
		this.i5 = i5;
	}

	public int getI6() {
		return i6;
	}

	public void setI6(int i6) {
		this.i6 = i6;
	}

	public int getI7() {
		return i7;
	}

	public void setI7(int i7) {
		this.i7 = i7;
	}

	public int getI8() {
		return i8;
	}

	public void setI8(int i8) {
		this.i8 = i8;
	}

	public int getI9() {
		return i9;
	}

	public void setI9(int i9) {
		this.i9 = i9;
	}

	public int getI10() {
		return i10;
	}

	public void setI10(int i10) {
		this.i10 = i10;
	}

	public boolean isScary() {
		return scary;
	}

	public void setScary(boolean scary) {
		this.scary = scary;
	}

	public void setScary(int scary) {
		this.scary = scary == 1;
	}

	public long getTs() {
		return ts;
	}

	public void setTs(long ts) {
		this.ts = ts;
	}

	public Date getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(Date timeStamp) {
		this.timeStamp = timeStamp;
	}

}
