package com.simra.konsumgandalf.profiles.services;

import com.simra.konsumgandalf.common.models.interfaces.ExistenceChecker;
import com.simra.konsumgandalf.common.utils.services.FileReaderService;
import com.simra.konsumgandalf.common.utils.services.ProfileBloomFilterService;
import com.simra.konsumgandalf.profiles.repositories.ProfileRepository;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class BloomFilterProfileExistenceChecker implements ExistenceChecker {

	@Autowired
	private ProfileBloomFilterService bloomFilterService;

	@Autowired
	private ProfileRepository profileRepository;

	@Autowired
	private FileReaderService fileReaderService;

	private static final Logger _logger = LoggerFactory.getLogger(BloomFilterProfileExistenceChecker.class);

	@PostConstruct
	public void loadFromDB() {
		if (bloomFilterService.size() >= 3_000) {
			return;
		}
		List<String> allPath = profileRepository.findAllPaths();
		bloomFilterService.add(allPath);
	}

	public boolean shouldBeProcessed(String path) {
		Date lastModifiedFile = fileReaderService.getFileLastModified(path);
		boolean inBloom = bloomFilterService.mightContain(path);

		if (!inBloom) {
			_logger.debug("Profile with path {} NOT in bloom → needs processing", path);
			return true;
		}

		Optional<Date> maybeLastModified = profileRepository.lastModified(path);
		if (maybeLastModified.isEmpty()) {
			_logger.debug("Profile with path {} NOT in DB → needs processing", path);
			return true;
		}

		Date lastModifiedDB = maybeLastModified.get();
		boolean fileIsNewer = lastModifiedFile.after(lastModifiedDB);
		_logger.debug("Profile with path {} is {}", path, fileIsNewer ? "outdated → needs update" : "up-to-date");

		return fileIsNewer;
	}

	public void add(String path) {
		bloomFilterService.add(path);
	}

}
