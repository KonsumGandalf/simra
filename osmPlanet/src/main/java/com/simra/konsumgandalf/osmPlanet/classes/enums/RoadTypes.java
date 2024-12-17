package com.simra.konsumgandalf.osmPlanet.classes.enums;

/**
 * Represents the different types of roads that can be found in the OSM data.
 * https://wiki.openstreetmap.org/wiki/Key:highway
 */
public enum RoadTypes {
    MOTORWAY("motorway"),
    MOTORWAY_LINK("motorway_link"),

    TRUNK("trunk"),
    TRUNK_LINK("trunk_link"),

    PRIMARY("primary"),
    PRIMARY_LINK("primary_link"),

    SECONDARY("secondary"),
    SECONDARY_LINK("secondary_link"),

    TERTIARY("tertiary"),
    TERTIARY_LINK("tertiary_link"),

    // from here everything should be displayed => no need to filter for highway types

    UNCLASSIFIED("unclassified"),
    RESIDENTIAL("residential"),
    LIVING_STREET("living_street"),
    SERVICE("service"),
    BRIDLEWAY("bridleway"),;

    public final String type;

    private RoadTypes(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
