package com.simra.konsumgandalf.rides.models.dtos;


import org.geolatte.geom.Point;

import java.util.Map;

public interface RideIncidentDTO{
	long getId();
	long getLat();
	long getLng();
	boolean getScary();
}
