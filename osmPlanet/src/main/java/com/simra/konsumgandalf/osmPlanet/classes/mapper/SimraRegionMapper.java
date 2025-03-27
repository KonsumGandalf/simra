package com.simra.konsumgandalf.osmPlanet.classes.mapper;

import java.util.HashMap;
import java.util.List;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SimraRegionMapper {

	public final Map<String, List<String>> map = new HashMap<>();

	public SimraRegionMapper() {
		map.put("Berlin-Potsdam", List.of("Berlin", "Potsdam"));
		map.put("London", List.of("London"));
		map.put("Bern", List.of("Bern"));
		map.put("Pforzheim-Enzkreis", List.of("Pforzheim", "Enzkreis"));
		map.put("Augsburg", List.of("Augsburg"));
		map.put("Ruhr-Region", List.of("Ruhrgebiet", "Essen", "Dortmund", "Bochum", "Duisburg", "Gelsenkirchen"));
		map.put("Stuttgart", List.of("Stuttgart"));
		map.put("Leipzig", List.of("Leipzig"));
		map.put("Wuppertal-Solingen-Remscheid", List.of("Wuppertal", "Solingen", "Remscheid"));
		map.put("Düsseldorf", List.of("Düsseldorf"));
		map.put("Eichwalde-Zeuthen-Schulzendorf", List.of("Eichwalde", "Zeuthen", "Schulzendorf"));
		map.put("Hannover", List.of("Hannover"));
		map.put("Bielefeld", List.of("Bielefeld"));
		map.put("Munich", List.of("Munich"));
		map.put("ZES-Experimental", List.of("ZES Experimental"));
		map.put("Konstanz", List.of("Konstanz"));
		map.put("Weimar", List.of("Weimar"));
	}

	public List<String> getRegionsForSimraRegion(String simraRegion) {
		return map.getOrDefault(simraRegion, List.of());
	}

}
