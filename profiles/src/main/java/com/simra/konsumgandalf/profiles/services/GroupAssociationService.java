package com.simra.konsumgandalf.profiles.services;

import com.simra.konsumgandalf.common.models.entities.SimraRegionGroupAssociation;
import com.simra.konsumgandalf.profiles.repositories.GroupAssociationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class GroupAssociationService {

	@Autowired
	private GroupAssociationRepository groupAssociationRepository;

	public Optional<SimraRegionGroupAssociation[]> getSafetyMetricsProfileBySimraRegion(String name) {
		return groupAssociationRepository.findBySimraRegion(name);
	}

}
