package com.simra.konsumgandalf.common.models.entities;


import com.opencsv.bean.CsvBindByName;
import jakarta.persistence.Id;
import jakarta.persistence.*;


@Entity
public class RideIncident {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private RideEntity rideEntity;

    @CsvBindByName(column = "lat")
    private double lat;

    @CsvBindByName(column = "lon")
    private double lng;

    @CsvBindByName(column = "ts")
    private String ts;

    @CsvBindByName(column = "bike")
    private Integer bike;

    @CsvBindByName(column = "childCheckBox")
    private Integer childCheckBox;

    @CsvBindByName(column = "trailerCheckBox")
    private Integer trailerCheckBox;

    @CsvBindByName(column = "pLoc")
    private Integer pLoc;

    @CsvBindByName(column = "incident")
    private Integer incident;

    @CsvBindByName(column = "desc")
    private String description;

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

    public String getTs() {
        return ts;
    }

    public void setTs(String ts) {
        this.ts = ts;
    }

    public Integer getBike() {
        return bike;
    }

    public void setBike(Integer bike) {
        this.bike = bike;
    }

    public Integer getChildCheckBox() {
        return childCheckBox;
    }

    public void setChildCheckBox(Integer childCheckBox) {
        this.childCheckBox = childCheckBox;
    }

    public Integer getTrailerCheckBox() {
        return trailerCheckBox;
    }

    public void setTrailerCheckBox(Integer trailerCheckBox) {
        this.trailerCheckBox = trailerCheckBox;
    }

    public Integer getpLoc() {
        return pLoc;
    }

    public void setpLoc(Integer pLoc) {
        this.pLoc = pLoc;
    }

    public Integer getIncident() {
        return incident;
    }

    public void setIncident(Integer incident) {
        this.incident = incident;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String desc) {
        this.description = desc;
    }
}
