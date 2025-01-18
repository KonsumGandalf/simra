package com.simra.konsumgandalf.common.models.maps;

import com.google.common.collect.ImmutableBiMap;
import com.simra.konsumgandalf.common.models.entities.RideIncident;
import com.simra.konsumgandalf.common.models.enums.ParticipantType;

import java.util.Map;
import java.util.function.Function;

/**
 * This class maps the Ix(i1,i2...) values from the RideIncident class to the
 * ParticipantType enum.
 */
public class IxFunctionToParticipantTypeMap {

	public static final Map<Function<RideIncident, Integer>, ParticipantType> IxFunctionToParticipantType = ImmutableBiMap
		.of(RideIncident::getI1, ParticipantType.BUS_COACH, RideIncident::getI2, ParticipantType.CYCLIST,
				RideIncident::getI3, ParticipantType.PEDESTRIAN, RideIncident::getI4, ParticipantType.DELIVERY_VAN,
				RideIncident::getI5, ParticipantType.LORRY_TRUCK, RideIncident::getI6, ParticipantType.MOTORCYCLIST,
				RideIncident::getI7, ParticipantType.CAR, RideIncident::getI8, ParticipantType.TAXI_CAB,
				RideIncident::getI9, ParticipantType.OTHER, RideIncident::getI10, ParticipantType.ELECTRIC_SCOOTER);

}
