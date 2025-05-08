package com.simra.konsumgandalf.common.utils.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class ProfileBloomFilterService extends BloomFilterService {

	public ProfileBloomFilterService(@Value("${BLOOM_FILTER_FILE_PROFILES}") String filePath,
			@Value("${BLOOM_FILTER_EXPECTED_INSERTIONS_PROFILES}") int expectedInsertions,
			@Value("${BLOOM_FILTER_ERROR_RATE_PROFILES}") double errorRate) {
		super(filePath, expectedInsertions, errorRate);
	}

}
