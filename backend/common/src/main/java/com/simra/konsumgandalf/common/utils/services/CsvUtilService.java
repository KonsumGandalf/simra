package com.simra.konsumgandalf.common.utils.services;

import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;

import org.springframework.stereotype.Service;

import java.io.StringReader;
import java.util.List;

@Service
public class CsvUtilService {

    /**
     * Parse CSV file to a list of model objects
     *
     * @param csvContent Path to the CSV file
     * @param clazz    Class of the model object
     * @return List of model objects
     */
    public <T> List<T> parseCsvToModel(String csvContent, Class<T> clazz) {
        try (StringReader reader = new StringReader(csvContent)) {
            CsvToBean<T> csvToBean = new CsvToBeanBuilder<T>(reader)
                    .withType(clazz)
                    .withIgnoreLeadingWhiteSpace(true)
                    .withThrowExceptions(false) // Add this line to ignore exceptions
                    .build();

            return csvToBean.parse();
        } catch (Exception e) {
            throw new RuntimeException("Error reading CSV content", e);
        }
    }
}
