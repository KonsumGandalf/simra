package com.simra.konsumgandalf.rides.models.dtos;

import com.simra.konsumgandalf.common.models.classes.Coordinate;

public class WithInRangeDTO extends Coordinate {

	private double range;

	public WithInRangeDTO() {
	}

	public WithInRangeDTO(double lat, double lon, double range) {
		super(lat, lon);
		this.range = range;
	}

	public double getRange() {
		return range;
	}

	public void setRange(double range) {
		this.range = range;
	}

}
