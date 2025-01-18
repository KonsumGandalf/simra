package com.simra.konsumgandalf.osmPlanet.classes.dtos;

import com.simra.konsumgandalf.common.models.entities.PlanetOsmLine;

public class FindStreetsWithNumberOfRidesDTO {

	private PlanetOsmLine planetOsmLine;

	private int numberOfRides;

	public FindStreetsWithNumberOfRidesDTO(PlanetOsmLine planetOsmLine, int numberOfRides) {
		this.planetOsmLine = planetOsmLine;
		this.numberOfRides = numberOfRides;
	}

	public PlanetOsmLine getPlanetOsmLine() {
		return planetOsmLine;
	}

	public void setPlanetOsmLine(PlanetOsmLine planetOsmLine) {
		this.planetOsmLine = planetOsmLine;
	}

	public int getNumberOfRides() {
		return numberOfRides;
	}

	public void setNumberOfRides(int numberOfRides) {
		this.numberOfRides = numberOfRides;
	}

}
