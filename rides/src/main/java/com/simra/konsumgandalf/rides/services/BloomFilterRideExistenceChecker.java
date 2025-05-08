package com.simra.konsumgandalf.rides.services;

import com.simra.konsumgandalf.common.models.interfaces.ExistenceChecker;
import com.simra.konsumgandalf.common.utils.services.RideBloomFilterService;
import com.simra.konsumgandalf.rides.repositories.RideEntityRepository;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BloomFilterRideExistenceChecker implements ExistenceChecker {

	@Autowired
	private RideBloomFilterService bloomFilterService;

	@Autowired
	private RideEntityRepository rideEntityRepository;

	private static final Logger _logger = LoggerFactory.getLogger(BloomFilterRideExistenceChecker.class);

	@PostConstruct
	public void loadFromDB() {
		if (bloomFilterService.size() >= 50_000) {
			return;
		}
		List<String> allPath = rideEntityRepository.findAllPaths();
		bloomFilterService.add(allPath);
	}

	public boolean doesNotExist(String path) {
		boolean existInBloom = bloomFilterService.mightContain(path);
		if (existInBloom) {
			_logger.debug("Ride with path {} exists in bloom filter", path);
			return false;
		}

		return this.doesNotExistInDB(path);
	}

	public boolean doesNotExistInDB(String path) {
		boolean existInDB = rideEntityRepository.existsByPath(path);
		if (existInDB) {
			_logger.debug("Ride with path {} exists in DB", path);
			bloomFilterService.add(path);
			return false;
		}

		_logger.debug("Ride with path {} does not exist", path);
		return true;
	}

	public void add(String path) {
		bloomFilterService.add(path);
	}

}
