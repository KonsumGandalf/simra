package com.simra.konsumgandalf.common.utils.services;

import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnels;
import com.simra.konsumgandalf.common.constants.CronExpressions;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.charset.StandardCharsets;

@Service
public class BloomFilterService {

	private static String BLOOM_FILTER_FILE_PATH;

	private static int EXPECTED_INSERTIONS;

	private static double ERROR_RATE;

	private BloomFilter<String> bloomFilter;

	private static final Logger _logger = LoggerFactory.getLogger(BloomFilterService.class);

	public BloomFilterService(@Value("${BLOOM_FILTER_FILE}") String filePath,
			@Value("${BLOOM_FILTER_EXPECTED_INSERTIONS}") int expectedInsertions,
			@Value("${BLOOM_FILTER_ERROR_RATE}") double fpp) {
		if (filePath == null) {
			return;
		}
		BLOOM_FILTER_FILE_PATH = filePath;
		EXPECTED_INSERTIONS = expectedInsertions;
		ERROR_RATE = fpp;

		bloomFilter = BloomFilter.create(Funnels.stringFunnel(StandardCharsets.UTF_8), EXPECTED_INSERTIONS, ERROR_RATE);
	}

	@Scheduled(cron = CronExpressions.EVERY_MINUTE)
	@PreDestroy
	public void saveToDisk() throws IOException {
		try (FileOutputStream fos = new FileOutputStream(BLOOM_FILTER_FILE_PATH)) {
			bloomFilter.writeTo(fos);
			_logger.info("Bloom filter saved to disk");
		}
	}

	@PostConstruct
	public void loadFromDisk() throws IOException, ClassNotFoundException {
		File file = new File(BLOOM_FILTER_FILE_PATH);
		if (file.exists()) {
			// If the file exists, read the Bloom filter from it
			try (FileInputStream fis = new FileInputStream(file)) {
				bloomFilter = BloomFilter.readFrom(fis, Funnels.stringFunnel(StandardCharsets.UTF_8));
				_logger.info("Bloom filter loaded from disk");
			}
			catch (IOException e) {
				_logger.error("Corrupted Bloom filter file, creating a new one.");
				bloomFilter = BloomFilter.create(Funnels.stringFunnel(StandardCharsets.UTF_8), EXPECTED_INSERTIONS,
						ERROR_RATE);
			}
		}
		else {
			// If the file does not exist, create a new empty Bloom filter
			_logger.info("No Bloom filter file found, creating a new one.");
			// The Bloom filter is already initialized in the constructor, so nothing else
			// is needed here
		}
	}

	public void add(String element) {
		bloomFilter.put(element);
	}

	/**
	 * Bloom filter might contain an element but there's always a chance of false positive
	 * @param element
	 * @return
	 */
	public boolean mightContain(String element) {
		return bloomFilter.mightContain(element);
	}

}
