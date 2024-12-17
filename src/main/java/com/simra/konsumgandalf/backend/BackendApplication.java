package com.simra.konsumgandalf.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Controller;

@SpringBootApplication
@EnableJpaRepositories(basePackages = {
		"com.simra.konsumgandalf.rides.repositories",
		"com.simra.konsumgandalf.osmPlanet.repositories"
})
@ComponentScan(basePackages = {
		"com.simra.konsumgandalf.osmPlanet.initializers",
		"com.simra.konsumgandalf.common.utils.services",
		"com.simra.konsumgandalf.rides.controllers",
		"com.simra.konsumgandalf.rides.services",
		"com.simra.konsumgandalf.osmrBackend.services",
		"com.simra.konsumgandalf.osmPlanet.services",
		"com.simra.konsumgandalf.osmPlanet.controller",
		"com.simra.konsumgandalf.backend.config"
})
@EntityScan(basePackages = {
		"com.simra.konsumgandalf.rides.models.entities",
		"com.simra.konsumgandalf.common.models.entities"
})
public class BackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(BackendApplication.class, args);
	}

}
