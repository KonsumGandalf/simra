package com.simra.konsumgandalf.common.models.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import org.locationtech.jts.geom.Geometry;

@Entity
public class PlanetOsmPolygon {

	@Id
	private Long osmId;

	@Column
	private String boundary;

	@Column
	private String adminLevel;

	@Column
	private String name;

	@Column(name = "way", columnDefinition = "geometry")
	private Geometry way;

}
