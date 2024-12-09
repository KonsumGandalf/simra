package com.simra.konsumgandalf.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = {
		"com.simra.konsumgandalf.rides.repositories"
})
@ComponentScan(basePackages = {
		"com.simra.konsumgandalf.osmPlanet.initializers",
		"com.simra.konsumgandalf.common.utils.services",
		"com.simra.konsumgandalf.rides.controllers",
		"com.simra.konsumgandalf.rides.services",
		"com.simra.konsumgandalf.osmrBackend.services"
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
