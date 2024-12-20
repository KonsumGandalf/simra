package com.simra.konsumgandalf.common.utils.services;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.springframework.stereotype.Service;

@Service
public class FileReaderService {

	/**
	 * Read file from path or URL
	 * @param path
	 * @return
	 */
	public String readFileFromPath(String path) {
		try {
			if (path.startsWith("http://") || path.startsWith("https://")) {
				try (InputStream in = new URL(path).openStream()) {
					return new String(in.readAllBytes());
				}
			}
			else {
				return new String(Files.readAllBytes(Paths.get(path)));
			}
		}
		catch (Exception e) {
			throw new RuntimeException("Error reading file", e);
		}
	}

	/**
	 * Process all files in a specified directory
	 * @param dirPath - Path to the unzipped directory
	 */
	public void processFilesFromDirectory(String dirPath) {
		try {
			Path directory = Paths.get(dirPath);
			if (!Files.isDirectory(directory)) {
				throw new IllegalArgumentException("Provided path is not a directory: " + dirPath);
			}

			// Traverse and process each file in the directory
			Files.walk(directory).filter(Files::isRegularFile).forEach(filePath -> {
				String fileContent = readFileFromPath(filePath.toString());
				// Process each file as needed
				System.out.println("Processing file: " + filePath);
				System.out.println("Content:\n" + fileContent);
				// Call your custom processing method here if needed
			});

		}
		catch (IOException e) {
			throw new RuntimeException("Error processing directory", e);
		}
	}

}
