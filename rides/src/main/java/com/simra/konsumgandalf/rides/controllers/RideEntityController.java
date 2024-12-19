package com.simra.konsumgandalf.rides.controllers;

import com.simra.konsumgandalf.common.logging.LogExecutionTime;
import com.simra.konsumgandalf.common.models.entities.RideCleanedLocation;
import com.simra.konsumgandalf.common.models.entities.RideEntity;
import com.simra.konsumgandalf.rides.services.RideEntityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@RestController
@RequestMapping("/rides")
public class RideEntityController {
    @Autowired
    private RideEntityService rideEntityService;

    @PostMapping("")
    public void loadAllPreviousRides() throws Exception {
        ExecutorService executorService = Executors.newFixedThreadPool(16);
        List<Callable<String>> tasks = new ArrayList<>();

        Path dataPath = Paths.get("")
                .toAbsolutePath()
                .getParent()
                .getParent()
                .getParent()
                .resolve("data/dataset-master")
                .normalize();

        Files.walk(dataPath)
                .filter(Files::isRegularFile)
                .filter(path -> path.getFileName().toString().startsWith("VM"))
                .forEach(path -> tasks.add(() -> {
                    try {
                        System.out.println("Processing file: " + path.toString());
                        RideEntity rideEntity = rideEntityService.generateNewRideEntity(path.toString());
                        return "Processed file: " + path.toString();
                    } catch (Exception e) {
                        System.err.println("Error processing file: " + path + " - " + e.getMessage());
                        return "Error processing file: " + path.toString();
                    }
                }));

        List<Future<String>> futures = executorService.invokeAll(tasks);

        // Wait for results
        for (Future<String> future : futures) {
            String result = future.get();
            System.out.println(result);
        }

        executorService.shutdown();
    }

    @GetMapping("/findAll/{id}")
    public List<RideCleanedLocation> getAllCleanedRides(@PathVariable Long id) {
        List<RideCleanedLocation> cleanedRides = rideEntityService.findAllCleanedRides(id);
        return cleanedRides;
    }
}
