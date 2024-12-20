package com.simra.konsumgandalf.common.utils.services;

import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import org.springframework.stereotype.Service;

import java.io.StringReader;
import java.util.List;

@Service
public class CsvUtilService {

	/**
	 * Parse CSV file to a list of model objects
	 * @param csvContent Path to the CSV file
	 * @param clazz Class of the model object
	 * @return List of model objects
	 */
	public <T> List<T> parseCsvToModel(String csvContent, Class<T> clazz) {
		return parseCsvToModel(csvContent, clazz, false);
	}

	/**
	 * Parse CSV file to a list of model objects
	 * @param csvContent - CSV content
	 * @param clazz - Class of the model object
	 * @param retrying - whether the method is being called after a failed attempt
	 * @return List of model objects
	 * @param <T>
	 */
	private <T> List<T> parseCsvToModel(String csvContent, Class<T> clazz, boolean retrying) {
		try (StringReader reader = new StringReader(csvContent)) {
			CsvToBean<T> csvToBean = new CsvToBeanBuilder<T>(reader).withType(clazz)
				.withIgnoreLeadingWhiteSpace(true)
				.withIgnoreEmptyLine(true)
				.build();

			return csvToBean.parse();
		}
		catch (RuntimeException e) {
			if (e.getCause() instanceof CsvRequiredFieldEmptyException) {
				if (retrying) {
					throw new RuntimeException("Error reading CSV content after cleanup", e);
				}

				String cleanedCsv = cleanCsvContent(csvContent);
				return parseCsvToModel(cleanedCsv, clazz, true);
			}
			throw e;
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Clean CSV content by f.e. removing trailing commas
	 * @param csvContent
	 * @return
	 */
	private String cleanCsvContent(String csvContent) {
		return csvContent.lines().map(line -> {
			// Check if the line ends with a comma and add another one if true
			if (line.endsWith(",")) {
				return line + ","; // Add a trailing comma
			}
			return line; // No change if there's no trailing comma
		})
			.reduce((l1, l2) -> l1 + "\n" + l2) // Join the lines back together
			.orElse(""); // Return an empty string if there's no content
	}

}
