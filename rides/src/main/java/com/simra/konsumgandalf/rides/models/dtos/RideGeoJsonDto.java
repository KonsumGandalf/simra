package com.simra.konsumgandalf.rides.models.dtos;

public record RideGeoJsonDto(Long rideId, String visitedWay, String assignedWay, String incidentWay,
		String incidentLocation) {
}
