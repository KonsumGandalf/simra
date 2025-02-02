package com.simra.konsumgandalf.common.utils.services;

import org.springframework.stereotype.Service;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

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

}
