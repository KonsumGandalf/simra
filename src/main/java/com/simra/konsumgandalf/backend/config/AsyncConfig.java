package com.simra.konsumgandalf.backend.config;

import com.simra.konsumgandalf.rides.controllers.RideEntityController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Future;

@Configuration
@EnableAsync
public class AsyncConfig {

	private static final Logger _logger = LoggerFactory.getLogger(AsyncConfig.class);

	@Bean(name = "taskExecutor")
	public ThreadPoolTaskExecutor taskExecutor() {
		_logger.info("Creating Background Async Task Executor");
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(8);
		executor.setMaxPoolSize(16);
		executor.setThreadNamePrefix("Async-");
		executor.initialize();
		return executor;
	}

}
