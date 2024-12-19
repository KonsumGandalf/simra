package com.simra.konsumgandalf.osmPlanet.classes.dtos;

import com.simra.konsumgandalf.common.models.classes.Coordinate;

public class GetHighwayInformationDTO {
    private Coordinate coordinate;
    private int zoom;

    public GetHighwayInformationDTO(Coordinate coordinate, int zoom) {
        this.coordinate = coordinate;
        this.zoom = zoom;
    }

    public GetHighwayInformationDTO() {
    }

    public Coordinate getCoordinate() {
        return coordinate;
    }

    public int getZoom() {
        return zoom;
    }
}
