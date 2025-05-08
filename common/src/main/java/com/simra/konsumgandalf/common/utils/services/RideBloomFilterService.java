package com.simra.konsumgandalf.common.utils.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class RideBloomFilterService extends BloomFilterService {

	public RideBloomFilterService(@Value("${BLOOM_FILTER_FILE_RIDES}") String filePath,
			@Value("${BLOOM_FILTER_EXPECTED_INSERTIONS_RIDES}") int expectedInsertions,
			@Value("${BLOOM_FILTER_ERROR_RATE_RIDES}") double errorRate) {
		super(filePath, expectedInsertions, errorRate);
	}

}
