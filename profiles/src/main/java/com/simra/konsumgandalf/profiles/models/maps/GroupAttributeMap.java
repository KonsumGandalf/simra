package com.simra.konsumgandalf.profiles.models.maps;

import com.simra.konsumgandalf.common.models.entities.Profile;
import com.simra.konsumgandalf.common.models.enums.SafetyMetricsProfileGroup;

import java.util.HashMap;
import java.util.function.Function;

public class GroupAttributeMap {

	private final HashMap<SafetyMetricsProfileGroup, Function<Profile, String>> map = new HashMap<>();

	public GroupAttributeMap() {
		map.put(SafetyMetricsProfileGroup.AGE, profile -> profile.getAgeGroup().name());
		map.put(SafetyMetricsProfileGroup.GENDER, profile -> profile.getGender().name());
		map.put(SafetyMetricsProfileGroup.BEHAVIOR, profile -> Integer.toString(profile.getBehaviour()));
		map.put(SafetyMetricsProfileGroup.EXPERIENCE, profile -> profile.getExperienceGroup().name());
	}

	public HashMap<SafetyMetricsProfileGroup, Function<Profile, String>> getMap() {
		return map;
	}

}
