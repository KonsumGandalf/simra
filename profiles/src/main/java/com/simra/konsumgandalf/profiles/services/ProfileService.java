package com.simra.konsumgandalf.profiles.services;

import com.simra.konsumgandalf.common.models.entities.Profile;
import com.simra.konsumgandalf.common.models.entities.SimraRegion;
import com.simra.konsumgandalf.common.models.maps.SimraRegionEnumNameMapper;
import com.simra.konsumgandalf.common.utils.services.BloomFilterService;
import com.simra.konsumgandalf.common.utils.services.CsvUtilService;
import com.simra.konsumgandalf.common.utils.services.FileReaderService;
import com.simra.konsumgandalf.profiles.repositories.ProfileRepository;
import com.simra.konsumgandalf.profiles.repositories.ProfileSimraRegionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Service
@Transactional
public class ProfileService {

	private static Path dataPath;

	@Autowired
	private FileReaderService fileReaderService;

	@Autowired
	private BloomFilterService bloomFilterService;

	@Autowired
	private ProfileRepository profileRepository;

	@Autowired
	private CsvUtilService csvUtilService;

	@Autowired
	private ProfileSimraRegionRepository profileSimraRegionRepository;

	private static final Logger _logger = LoggerFactory.getLogger(ProfileService.class);

	private static final SimraRegionEnumNameMapper simraRegionEnumNameMapper = new SimraRegionEnumNameMapper();

	ProfileService(@Value("${SIMRA_PROFILE_FILE_PATH:./}") Path filePath) {
		dataPath = filePath;
	}

	public void loadAllPrevProfiles() {
		List<CompletableFuture<Void>> futures = new ArrayList<>();

		try {
			Files.walk(dataPath, 8)
				.filter(Files::isRegularFile)
				.filter(FileReaderService::isEntityFile)
				.map(Path::toString)
				.filter(path -> !isProfileUpToDate(path))
				.forEach(path -> {
					CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
						try {
							_logger.info("Processing file: " + path.toString() + " on thread: "
									+ Thread.currentThread().getName());
							generateNewProfileEntity(path);
							bloomFilterService.add(path);
						}
						catch (Exception e) {
							_logger.error("Error processing file: " + path.toString(), e);
						}
					});
					futures.add(future);
				});
		}
		catch (Exception e) {
			_logger.error("Error loading profiles", e);
		}

		CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
	}

	public boolean isEmpty() {
		return profileRepository.count() == 0;
	}

	protected Profile generateProfileFromCsv(String path) {
		String fileContent = fileReaderService.readFileFromPath(path);

		fileContent = Arrays.stream(fileContent.split("\n"))
			.filter(line -> !line.contains("#"))
			.reduce((line1, line2) -> line1 + "\n" + line2)
			.orElse("");

		Optional<Profile> optionalProfile = csvUtilService.parseCsvToSingleModel(fileContent, Profile.class);
		if (optionalProfile.isEmpty()) {
			throw new RuntimeException("Error parsing CSV file");
		}

		Profile profile = optionalProfile.get();
		profile.setLastModified(fileReaderService.getFileLastModified(path));
		profile.setPath(path);

		Optional<String> regionName = simraRegionEnumNameMapper.getNameForEnum(profile.getSimraRegionGroup());
		if (regionName.isEmpty()) {
			_logger.error("Could not find region name for enum: " + profile.getSimraRegionGroup());
		}
		else {
			SimraRegion simraRegion = profileSimraRegionRepository.findByName(regionName.get()).orElseGet(() -> {
				return new SimraRegion(regionName.get());
			});
			profile.setSimraRegion(simraRegion);
		}

		return profile;
	}

	private Profile generateNewProfileEntity(String path) {
		Profile profile = generateProfileFromCsv(path);

		profileRepository.save(profile);
		return profile;
	}

	private boolean isProfileUpToDate(String path) {
		Date lastModifiedFile = fileReaderService.getFileLastModified(path);
		boolean doesExistInBloomFilter = bloomFilterService.mightContain(path);
		if (!doesExistInBloomFilter) {
			return false;
		}

		Optional<Date> maybeLastModified = profileRepository.lastModified(path);
		if (maybeLastModified.isEmpty()) {
			return false;
		}

		Date lastModifiedDB = maybeLastModified.get();
		return !lastModifiedDB.before(lastModifiedFile);
	}

}
