package com.simra.konsumgandalf.rides.services;

import com.simra.konsumgandalf.common.models.interfaces.ExistenceChecker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DatabaseExistenceChecker extends BloomFilterRideExistenceChecker implements ExistenceChecker {

	DatabaseExistenceChecker() {
		super();
	}

	@Override
	public boolean doesNotExist(String path) {
		return this.doesNotExistInDB(path);
	}

}
