package com.simra.konsumgandalf.osmPlanet.initializers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class OsmSchemaInitializer implements CommandLineRunner {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public void run(String... args) throws Exception {
        try {
            // Check if the table exists
            String tableCheckQuery = "SELECT EXISTS (" +
                    "SELECT FROM information_schema.tables " +
                    "WHERE table_name = 'planet_osm_line'" +
                    ")";
            Boolean tableExists = jdbcTemplate.queryForObject(tableCheckQuery, Boolean.class);

            if (Boolean.TRUE.equals(tableExists)) {
                // Check if 'id' is already a primary key
                String primaryKeyCheckQuery = "SELECT COUNT(*) " +
                        "FROM information_schema.table_constraints tc " +
                        "JOIN information_schema.key_column_usage kcu " +
                        "ON tc.constraint_name = kcu.constraint_name " +
                        "WHERE tc.table_name = 'planet_osm_line' " +
                        "AND tc.constraint_type = 'PRIMARY KEY' " +
                        "AND kcu.column_name = 'id'";
                Integer primaryKeyCount = jdbcTemplate.queryForObject(primaryKeyCheckQuery, Integer.class);

                if (primaryKeyCount == 0) {
                    // Add primary key to 'id' column
                    String alterTableQuery = "ALTER TABLE planet_osm_line ADD PRIMARY KEY (id);";
                    jdbcTemplate.execute(alterTableQuery);
                    System.out.println("Primary key added to planet_osm_line table.");
                } else {
                    System.out.println("Primary key already exists on the 'id' column in planet_osm_line.");
                }
            } else {
                System.out.println("Table 'planet_osm_line' does not exist. Skipping primary key addition.");
            }
        } catch (Exception e) {
            System.err.println("Failed to alter table: " + e.getMessage());
        }
    }
}