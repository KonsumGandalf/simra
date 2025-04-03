package com.simra.konsumgandalf.osmPlanet.classes.dtos;

import com.simra.konsumgandalf.common.models.entities.SafetyMetricsSimraRegion;

import java.util.List;

public interface SimraRegionCardDetailsDTO {

	String getName();

	List<SafetyMetricsSimraRegion> getSafetyMetricsSimraRegions();

	List<String> getRegions();

}
