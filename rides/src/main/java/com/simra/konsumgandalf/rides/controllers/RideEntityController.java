package com.simra.konsumgandalf.rides.controllers;

import com.simra.konsumgandalf.common.models.entities.RideEntity;
import com.simra.konsumgandalf.rides.services.RideEntityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/rides")
public class RideEntityController {

	@Autowired
	private RideEntityService rideEntityService;

	private static final Logger _logger = LoggerFactory.getLogger(RideEntityController.class);

	@Async
	@PostMapping("")
	public void loadAllPreviousRides() throws Exception {
		rideEntityService.loadAllPreviousRides();
	}

}
