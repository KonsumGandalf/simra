package com.simra.konsumgandalf.profiles.controllers;

import com.simra.konsumgandalf.common.models.entities.SimraRegionGroupAssociation;
import com.simra.konsumgandalf.profiles.services.AnalyticsProfileService;
import com.simra.konsumgandalf.profiles.services.GroupAssociationService;
import com.simra.konsumgandalf.profiles.services.ProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/group-association")
public class GroupAssociationController {

	@Autowired
	private GroupAssociationService groupAssociationService;

	@GetMapping("/{name}")
	public Optional<SimraRegionGroupAssociation[]> getSafetyMetricsProfileBySimraRegion(@PathVariable String name) {
		return groupAssociationService.getSafetyMetricsProfileBySimraRegion(name);
	}

}
