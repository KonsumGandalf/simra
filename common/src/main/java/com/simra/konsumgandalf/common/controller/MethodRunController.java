package com.simra.konsumgandalf.common.controller;

import com.simra.konsumgandalf.common.models.entities.MethodRun;
import com.simra.konsumgandalf.common.services.MethodRunService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/method-run")
public class MethodRunController {

	@Autowired
	private MethodRunService methodRunService;

	@GetMapping("{name}")
	public Optional<MethodRun> getLastRunOfRun(@PathVariable String name) {
		return methodRunService.getLastRunOfRun(name);
	}

}
