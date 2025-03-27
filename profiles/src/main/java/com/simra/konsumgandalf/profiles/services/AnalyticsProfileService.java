package com.simra.konsumgandalf.profiles.services;

import com.simra.konsumgandalf.common.logging.LogExecutionTime;
import com.simra.konsumgandalf.common.models.entities.Profile;
import com.simra.konsumgandalf.common.models.entities.SafetyMetricsProfile;
import com.simra.konsumgandalf.common.models.entities.SimraRegion;
import com.simra.konsumgandalf.common.models.entities.SimraRegionGroupAssociation;
import com.simra.konsumgandalf.common.models.enums.SafetyMetricsProfileGroup;
import com.simra.konsumgandalf.common.utils.ScoreUtils;
import com.simra.konsumgandalf.profiles.models.dtos.AggregatedSafetyMetricsDTO;
import com.simra.konsumgandalf.profiles.models.maps.GroupAttributeMap;
import com.simra.konsumgandalf.profiles.models.classes.SafeMetricsProfileKey;
import com.simra.konsumgandalf.profiles.repositories.GroupAssociationRepository;
import com.simra.konsumgandalf.profiles.repositories.ProfileRepository;
import com.simra.konsumgandalf.profiles.repositories.ProfileSimraRegionRepository;
import com.simra.konsumgandalf.profiles.repositories.SafetyMetricsProfileRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

@Transactional
@Service
public class AnalyticsProfileService {

	@Autowired
	private ProfileRepository profileRepository;

	@Autowired
	private ProfileSimraRegionRepository profileSimraRegionRepository;

	@Autowired
	private SafetyMetricsProfileRepository safetyMetricsProfileRepository;

	private final GroupAttributeMap groupAttributeMap = new GroupAttributeMap();

	@Autowired
	private GroupAssociationRepository groupAssociationRepository;

	private final Logger _logger = LoggerFactory.getLogger(AnalyticsProfileService.class);

	@Async
	@LogExecutionTime
	public void updateProfileSimraRegion() {
		List<CompletableFuture<Void>> listOfProcessedSimraRegions = new ArrayList<>();

		List<SimraRegion> allSimraRegions = profileSimraRegionRepository.findAll()
			.stream()
			.filter(simraRegion -> !simraRegion.getName().equals("All"))
			.toList();
		for (SimraRegion simraRegion : allSimraRegions) {
			CompletableFuture<Void> processedStreet = CompletableFuture.runAsync(() -> {
				updateSafetyMetrics(simraRegion);
			});
			listOfProcessedSimraRegions.add(processedStreet);
		}
		CompletableFuture.allOf(listOfProcessedSimraRegions.toArray(new CompletableFuture[0])).join();

		calculateAggregatedSafetyMetrics();
	}

	void updateSafetyMetrics(SimraRegion simraRegion) {
		List<Profile> profiles = profileRepository.findBySimraRegion(simraRegion);

		HashMap<SafeMetricsProfileKey, SafetyMetricsProfile> safetyMetricsProfileHashMap = new HashMap<>();
		for (Profile profile : profiles) {
			for (Map.Entry<SafetyMetricsProfileGroup, Function<Profile, String>> attribute : groupAttributeMap.getMap()
				.entrySet()) {
				SafeMetricsProfileKey safeMetricsProfileKey = new SafeMetricsProfileKey(attribute.getKey(),
						attribute.getValue().apply(profile));
				safetyMetricsProfileHashMap.compute(safeMetricsProfileKey, (k, v) -> {
					if (v == null) {
						return new SafetyMetricsProfile(k.groupKey(), profile.getNumberOfRides(),
								profile.getNumberOfIncidents(), profile.getNumberOfScaryIncidents());
					}
					else {
						v.setTotalIncidents(v.getTotalIncidents() + profile.getNumberOfIncidents());
						v.setTotalScaryIncidents(v.getTotalScaryIncidents() + profile.getNumberOfScaryIncidents());
						v.setTotalRides(v.getTotalRides() + profile.getNumberOfRides());
						return v;
					}
				});
			}
		}

		ArrayList<SafetyMetricsProfile> safetyMetricsProfiles = new ArrayList<>(safetyMetricsProfileHashMap.values());
		HashMap<SafetyMetricsProfileGroup, SimraRegionGroupAssociation> simraRegionGroupAssociations = getSimraRegionGroupAssociations(
				simraRegion);
		for (Map.Entry<SafeMetricsProfileKey, SafetyMetricsProfile> entry : safetyMetricsProfileHashMap.entrySet()) {
			SimraRegionGroupAssociation association = simraRegionGroupAssociations.get(entry.getKey().group());

			SafetyMetricsProfile sm = entry.getValue();

			float score = ScoreUtils.calculateDangerousScore(sm.getTotalRides(), sm.getTotalIncidents(),
					sm.getTotalScaryIncidents());
			sm.setDangerousScore(score);
			sm.setGroupName(entry.getKey().groupKey());
			sm.setRegionName(simraRegion.getName());
			sm.setGroupType(entry.getKey().group());
			sm.setSimraRegionGroupAssociation(association);

			safetyMetricsProfiles.add(sm);
		}

		safetyMetricsProfileRepository.deleteAllBySimraRegion(simraRegion.getName());
		safetyMetricsProfileRepository.saveAll(safetyMetricsProfiles);

		this._logger.info("Updated safety metrics for region: " + simraRegion.getName());
	}

	public void calculateAggregatedSafetyMetrics() {
		List<AggregatedSafetyMetricsDTO> aggregatedSafetyMetrics = profileRepository.getAggregatedSafetyMetrics();

		SimraRegion simraRegion = profileSimraRegionRepository.findByName("All").orElse(new SimraRegion("All"));

		HashMap<SafetyMetricsProfileGroup, SimraRegionGroupAssociation> simraRegionGroupAssociations = getSimraRegionGroupAssociations(
				simraRegion);
		ArrayList<SafetyMetricsProfile> safetyMetricsProfiles = new ArrayList<>();
		for (AggregatedSafetyMetricsDTO aggregatedSafetyMetric : aggregatedSafetyMetrics) {
			SafetyMetricsProfile safetyMetricsProfile = new SafetyMetricsProfile(aggregatedSafetyMetric.getGroupName(),
					aggregatedSafetyMetric.getTotalRides(), aggregatedSafetyMetric.getTotalIncidents(),
					aggregatedSafetyMetric.getTotalScaryIncidents());

			float dangerousScore = ScoreUtils.calculateDangerousScore(aggregatedSafetyMetric.getTotalRides(),
					aggregatedSafetyMetric.getTotalIncidents(), aggregatedSafetyMetric.getTotalScaryIncidents());
			safetyMetricsProfile.setDangerousScore(dangerousScore);
			safetyMetricsProfile.setRegionName(simraRegion.getName());
			safetyMetricsProfile.setGroupType(aggregatedSafetyMetric.getGroupType());
			safetyMetricsProfile.setSimraRegionGroupAssociation(
					simraRegionGroupAssociations.get(aggregatedSafetyMetric.getGroupType()));
			safetyMetricsProfiles.add(safetyMetricsProfile);
		}

		safetyMetricsProfileRepository.deleteAllBySimraRegion(simraRegion.getName());
		safetyMetricsProfileRepository.saveAll(safetyMetricsProfiles);

		this._logger.info("Updated aggregated safety metrics for the aggregated ALL regions");
	}

	private HashMap<SafetyMetricsProfileGroup, SimraRegionGroupAssociation> getSimraRegionGroupAssociations(
			SimraRegion simraRegion) {
		HashMap<SafetyMetricsProfileGroup, SimraRegionGroupAssociation> simraRegionGroupAssociations = new HashMap<>();
		for (SafetyMetricsProfileGroup group : SafetyMetricsProfileGroup.values()) {
			SimraRegionGroupAssociation simraRegionGroupAssociation = groupAssociationRepository
				.findBySimraRegionAndGroupType(simraRegion, group)
				.orElseGet(() -> {
					SimraRegionGroupAssociation newAssociation = new SimraRegionGroupAssociation(simraRegion, group);
					return groupAssociationRepository.save(newAssociation);
				});
			;
			simraRegionGroupAssociations.put(group, simraRegionGroupAssociation);
		}
		return simraRegionGroupAssociations;
	}

}
