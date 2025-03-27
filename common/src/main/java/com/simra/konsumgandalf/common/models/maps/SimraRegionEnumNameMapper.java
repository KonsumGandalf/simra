package com.simra.konsumgandalf.common.models.maps;

import com.google.common.collect.HashBiMap;
import com.simra.konsumgandalf.common.models.enums.SimraRegionGroup;

import java.util.Optional;

public class SimraRegionEnumNameMapper {

	private final HashBiMap<SimraRegionGroup, String> enumNameMap = HashBiMap.create();

	public SimraRegionEnumNameMapper() {
		enumNameMap.put(SimraRegionGroup.NOT_CHOSEN, "Please-Choose");
		enumNameMap.put(SimraRegionGroup.BERLIN_POTSDAM, "Berlin-Potsdam");
		enumNameMap.put(SimraRegionGroup.LONDON, "London");
		enumNameMap.put(SimraRegionGroup.OTHER, "Other");
		enumNameMap.put(SimraRegionGroup.BERN, "Bern");
		enumNameMap.put(SimraRegionGroup.PFORZHEIM_ENZKREIS, "Pforzheim-Enzkreis");
		enumNameMap.put(SimraRegionGroup.AUGSBURG, "Augsburg");
		enumNameMap.put(SimraRegionGroup.RUHR_REGION, "Ruhr-Region");
		enumNameMap.put(SimraRegionGroup.STUTTGART, "Stuttgart");
		enumNameMap.put(SimraRegionGroup.LEIPZIG, "Leipzig");
		enumNameMap.put(SimraRegionGroup.WUPPERTAL_SOLINGEN_REMSCHEID, "Wuppertal-Solingen-Remscheid");
		enumNameMap.put(SimraRegionGroup.DUESSELDORF, "Düsseldorf");
		enumNameMap.put(SimraRegionGroup.EICHWALDE_ZEUTHEN_SCHULZENDORF, "Eichwalde-Zeuthen-Schulzendorf");
		enumNameMap.put(SimraRegionGroup.HANNOVER, "Hannover");
		enumNameMap.put(SimraRegionGroup.BIELEFELD, "Bielefeld");
		enumNameMap.put(SimraRegionGroup.MUNICH, "Munich");
		enumNameMap.put(SimraRegionGroup.ZES_EXPERIMENTAL, "ZES-Experimental");
		enumNameMap.put(SimraRegionGroup.KONSTANZ, "Konstanz");
		enumNameMap.put(SimraRegionGroup.WEIMAR, "Weimar");
		enumNameMap.put(SimraRegionGroup.ALL, "All");
	}

	public Optional<String> getNameForEnum(SimraRegionGroup region) {
		return enumNameMap.get(region) != null ? Optional.of(enumNameMap.get(region)) : Optional.empty();
	}

	public Optional<SimraRegionGroup> getEnumForName(String label) {
		return enumNameMap.inverse().get(label) != null ? Optional.of(enumNameMap.inverse().get(label))
				: Optional.empty();
	}

}
