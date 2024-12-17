package com.simra.konsumgandalf.osmPlanet.classes.mapper;

import com.simra.konsumgandalf.osmPlanet.classes.enums.RoadTypes;

import java.util.*;

/**
 * This class maps zoom levels to road types that should be displayed at that zoom level.
 */
public class ZoomRoadTypeMapper {
    private final HashMap<Integer, List<RoadTypes>> map = new HashMap<>();
    public ZoomRoadTypeMapper() {
        // Makes no sense since bikes are not allowed on motorways
        // map.put(7, Arrays.asList(RoadTypes.MOTORWAY, RoadTypes.MOTORWAY_LINK));
        map.put(11, Arrays.asList(RoadTypes.PRIMARY, RoadTypes.PRIMARY_LINK));
        map.put(13, Arrays.asList(RoadTypes.SECONDARY, RoadTypes.SECONDARY_LINK));
        map.put(14, Arrays.asList(RoadTypes.TERTIARY, RoadTypes.TERTIARY_LINK));
        map.put(15, Arrays.asList(RoadTypes.UNCLASSIFIED, RoadTypes.RESIDENTIAL, RoadTypes.LIVING_STREET, RoadTypes.BRIDLEWAY));

        List<RoadTypes> cumulative = new ArrayList<>();
        for (int i = 1; i <= 17; i++) {
            if (map.containsKey(i)) {
                cumulative.addAll(map.get(i));
            }
            map.put(i, new ArrayList<>(cumulative));
        }
        for (int i = 18; i<= 30; i++) {
            map.put(i, Arrays.asList(RoadTypes.values()));
        }

    }

    public List<RoadTypes> getRoadTypes(int zoomLevel) {
        return map.get(zoomLevel);
    }
}

