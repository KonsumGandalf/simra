package com.simra.konsumgandalf.osmPlanet.controller;

import com.simra.konsumgandalf.common.models.entities.PlanetOsmLine;
import com.simra.konsumgandalf.osmPlanet.classes.dtos.GetHighwayInformationDTO;
import com.simra.konsumgandalf.osmPlanet.services.OsmHighwayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("streets")
public class OsmHighwayController {
    @Autowired
    private OsmHighwayService osmHighwayService;

    @GetMapping("")
    public List<Map<String, Object>> getHighwayInformation(
            @RequestParam("lat") double lat,
            @RequestParam("lng") double lng,
            @RequestParam("zoom") int zoom
    ) {
        return osmHighwayService.getHighwayInformation(lat, lng, zoom);
    }
}
