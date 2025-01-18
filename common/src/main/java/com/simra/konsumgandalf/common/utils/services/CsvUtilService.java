package com.simra.konsumgandalf.common.utils.services;

import com.opencsv.bean.BeanVerifier;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.StringReader;
import java.util.List;

@Service
public class CsvUtilService {

	private static final Logger _logger = LoggerFactory.getLogger(CsvUtilService.class);

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
	 * @param <T>
	 * @return List of model objects
	 */
	private <T> List<T> parseCsvToModel(String csvContent, Class<T> clazz, boolean retrying) {
		try (StringReader reader = new StringReader(csvContent)) {
			CsvToBean<T> csvToBean = new CsvToBeanBuilder<T>(reader).withType(clazz)
				.withIgnoreLeadingWhiteSpace(true)
				.withIgnoreEmptyLine(true)
				// @TODO: slow down the parsing process but ensures no
				.withOrderedResults(true)
				.build();

			return csvToBean.parse();
		}
		catch (RuntimeException e) {
			if (e.getCause() instanceof CsvRequiredFieldEmptyException) {
				if (retrying) {
					_logger.error("Error reading CSV content after cleanup", e);
				}

				String cleanedCsv = cleanCsvContent(csvContent);
				return parseCsvToModel(cleanedCsv, clazz, true);
			}
		}
		return List.of();
	}

	/**
	 * Clean CSV content by f.e. removing trailing commas
	 * @param csvContent
	 * @return
	 */
	private String cleanCsvContent(String csvContent) {
		long expectedCommas = getExpectedCommasFromHeader(csvContent);

		return csvContent.lines().map(line -> {
			// Check if the line ends with a comma and add another one if true
			if (line.endsWith(",")) {
				return line + ","; // Add a trailing comma
			}
			return line; // No change if there's no trailing comma
		}).filter(line -> isValidCsvLine(line, expectedCommas)).reduce((l1, l2) -> l1 + "\n" + l2).orElse(""); // Return
																												// an
																												// empty
																												// string
																												// if
																												// there's
																												// no
																												// content
	}

	private long getExpectedCommasFromHeader(String csvContent) {
		// Get the first line (header) of the CSV content
		String headerLine = csvContent.lines().findFirst().orElse("");

		// Count the number of commas in the header line to determine the number of fields
		return headerLine.chars().filter(ch -> ch == ',').count();
	}

	private boolean isValidCsvLine(String line, long expectedCommas) {
		// Count the number of commas in the line
		long commaCount = line.chars().filter(ch -> ch == ',').count();

		// Return true if the number of commas matches the expected number
		return commaCount == expectedCommas;
	}

}
