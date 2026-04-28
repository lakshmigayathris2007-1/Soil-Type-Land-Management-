package com.soilmanagement.filehandler;

import com.soilmanagement.domain.Soil;
import com.soilmanagement.domain.Land;
import com.soilmanagement.exception.InvalidSoilDataException;
import org.springframework.stereotype.Component;

import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.logging.Logger;

/**
 * SoilLandFileHandler
 * Handles reading and writing Soil and Land records to/from CSV flat files.
 *
 * Demonstrates:
 * - FileReader / FileWriter
 * - BufferedReader / BufferedWriter
 * - IOException and custom exception handling
 * - StringTokenizer for CSV parsing
 * - try-catch-finally blocks
 */
@Component
public class SoilLandFileHandler {

    private static final Logger LOGGER = Logger.getLogger(SoilLandFileHandler.class.getName());

    // File paths — relative to working directory
    public static final String SOIL_DATA_FILE = "data/soil_records.csv";
    public static final String LAND_DATA_FILE = "data/land_records.csv";
    public static final String REPORT_LOG_FILE = "data/report_log.txt";

    // CSV header for soil file
    private static final String SOIL_CSV_HEADER = "soilSampleCode,soilType,phLevel,organicCarbonPercent,nitrogenContentKgPerHa,"
            +
            "phosphorusContentKgPerHa,potassiumContentKgPerHa,waterHoldingCapacityPercent," +
            "electricalConductivityDsPerM,texture,bulkDensityGPerCm3,sampleCollectionDate,collectionLocation";

    /**
     * Write a list of Soil objects to a CSV file.
     * Uses BufferedWriter for performance (fewer disk I/O flushes).
     *
     * @throws IOException if file cannot be written
     */
    public void writeSoilRecords(List<Soil> soils, String filePath) throws IOException {
        // Ensure parent directory exists
        new File(filePath).getParentFile().mkdirs();

        BufferedWriter writer = null; // declared outside try for finally block access
        try {
            // FileWriter in append=false mode (overwrite)
            writer = new BufferedWriter(new FileWriter(filePath, false));
            writer.write(SOIL_CSV_HEADER);
            writer.newLine();

            for (Soil soil : soils) {
                // StringBuilder builds each CSV line efficiently
                StringBuilder line = new StringBuilder();
                line.append(nullSafe(soil.getSoilSampleCode())).append(",")
                        .append(nullSafe(soil.getSoilType())).append(",")
                        .append(nullSafe(soil.getPhLevel())).append(",")
                        .append(nullSafe(soil.getOrganicCarbonPercent())).append(",")
                        .append(nullSafe(soil.getNitrogenContentKgPerHa())).append(",")
                        .append(nullSafe(soil.getPhosphorusContentKgPerHa())).append(",")
                        .append(nullSafe(soil.getPotassiumContentKgPerHa())).append(",")
                        .append(nullSafe(soil.getWaterHoldingCapacityPercent())).append(",")
                        .append(nullSafe(soil.getElectricalConductivityDsPerM())).append(",")
                        .append(nullSafe(soil.getTexture())).append(",")
                        .append(nullSafe(soil.getBulkDensityGPerCm3())).append(",")
                        .append(nullSafe(soil.getSampleCollectionDate())).append(",")
                        .append(nullSafe(soil.getCollectionLocation()));
                writer.write(line.toString());
                writer.newLine();
            }

            LOGGER.info("Successfully wrote " + soils.size() + " soil records to " + filePath);

        } catch (IOException e) {
            LOGGER.severe("Failed to write soil records: " + e.getMessage());
            throw e; // re-throw after logging
        } finally {
            // finally block always closes the writer, preventing resource leaks
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException closeEx) {
                    LOGGER.warning("Writer close failed: " + closeEx.getMessage());
                }
            }
        }
    }

    /**
     * Read Soil records from a CSV file.
     * Uses BufferedReader for efficient line-by-line reading.
     * Uses StringTokenizer to parse each CSV row.
     *
     * @throws IOException if file cannot be read
     */
    public List<Soil> readSoilRecords(String filePath) throws IOException {
        List<Soil> soils = new ArrayList<>(); // ArrayList — ordered collection
        BufferedReader reader = null;

        try {
            reader = new BufferedReader(new FileReader(filePath));
            String line;
            boolean firstLine = true;

            while ((line = reader.readLine()) != null) {
                // Skip header line
                if (firstLine) {
                    firstLine = false;
                    continue;
                }
                if (line.isBlank())
                    continue;

                // StringTokenizer parses comma-delimited CSV fields
                StringTokenizer tokenizer = new StringTokenizer(line, ",");
                try {
                    Soil soil = parseSoilFromTokenizer(tokenizer);
                    soils.add(soil);
                } catch (IllegalArgumentException e) {
                    // Handle per-row parse errors without aborting the whole file
                    LOGGER.warning("Skipping malformed soil row: " + line + " — " + e.getMessage());
                }
            }

            LOGGER.info("Read " + soils.size() + " soil records from " + filePath);

        } catch (FileNotFoundException e) {
            LOGGER.warning("Soil data file not found: " + filePath);
            throw new IOException("Soil data file not found: " + filePath, e);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException closeEx) {
                    LOGGER.warning("Reader close failed: " + closeEx.getMessage());
                }
            }
        }
        return soils;
    }

    /**
     * Parse a Soil object from a StringTokenizer.
     * Validates pH range; throws InvalidSoilDataException if invalid.
     */
    private Soil parseSoilFromTokenizer(StringTokenizer tok) {
        String sampleCode = nextToken(tok);
        String soilType = nextToken(tok);
        double ph = parseDouble(nextToken(tok));

        // Custom exception thrown when pH is out of valid range
        if (ph < 0 || ph > 14) {
            throw new InvalidSoilDataException("phLevel", ph,
                    "pH must be between 0 and 14; got: " + ph);
        }

        double oc = parseDouble(nextToken(tok));
        double n = parseDouble(nextToken(tok));
        double p = parseDouble(nextToken(tok));
        double k = parseDouble(nextToken(tok));
        double whc = parseDouble(nextToken(tok));
        double ec = parseDouble(nextToken(tok));
        String texture = nextToken(tok);
        double bulkDensity = parseDouble(nextToken(tok));
        String dateStr = nextToken(tok);
        String location = tok.hasMoreTokens() ? tok.nextToken().trim() : "";

        return Soil.builder()
                .soilSampleCode(sampleCode)
                .soilType(soilType)
                .phLevel(ph)
                .organicCarbonPercent(oc)
                .nitrogenContentKgPerHa(n)
                .phosphorusContentKgPerHa(p)
                .potassiumContentKgPerHa(k)
                .waterHoldingCapacityPercent(whc)
                .electricalConductivityDsPerM(ec)
                .texture(texture)
                .bulkDensityGPerCm3(bulkDensity)
                .sampleCollectionDate(dateStr.isBlank() ? null : LocalDate.parse(dateStr))
                .collectionLocation(location)
                .build();
    }

    /**
     * Append a single log entry to the report log file.
     * Uses FileWriter in append mode.
     */
    public void appendToReportLog(String logEntry) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(REPORT_LOG_FILE, true))) {
            writer.write(logEntry);
            writer.newLine();
        } catch (IOException e) {
            LOGGER.warning("Could not write to report log: " + e.getMessage());
        }
    }

    /**
     * Write sample seed data files for demonstration.
     * Creates data/ directory with realistic CSV content.
     */
    public void createSampleDataFiles() throws IOException {
        new File("data").mkdirs();

        // Soil sample data
        String soilSample = SOIL_CSV_HEADER + "\n" +
                "SS-KA-2024-001,Alluvial,6.8,1.85,180.5,22.3,145.0,42.0,0.45,Loamy,1.35,2024-01-15,Mysuru District\n" +
                "SS-KA-2024-002,Black,7.2,2.10,210.0,18.0,320.0,55.0,0.60,Clayey,1.28,2024-02-10,Dharwad District\n" +
                "SS-TN-2024-001,Red,5.9,0.95,120.0,10.5,95.0,30.0,0.30,Sandy Loam,1.52,2024-03-05,Coimbatore\n" +
                "SS-TN-2024-002,Laterite,5.5,0.75,90.0,8.0,75.0,25.0,0.25,Sandy,1.60,2024-03-20,Nilgiris\n" +
                "SS-MH-2024-001,Black,7.8,2.50,240.0,25.0,380.0,62.0,0.75,Clayey,1.20,2024-04-01,Nagpur District\n";

        try (BufferedWriter w = new BufferedWriter(new FileWriter(SOIL_DATA_FILE, false))) {
            w.write(soilSample);
        }

        // Land sample data
        String landCsvHeader = "surveyNumber,areaInAcres,village,taluk,district,state," +
                "latitudeCoordinate,longitudeCoordinate,landClassification,irrigationSourceType," +
                "elevationMeters,registrationDate,ownershipDocument\n";
        String landSample = landCsvHeader +
                "SY-KA-123-4A,5.25,Hullahalli,Nanjangud,Mysuru,Karnataka,12.12,76.69,Agricultural,Canal,780.0,2018-06-15,RTC-KA-12345\n"
                +
                "SY-TN-456-2B,3.00,Kinathukadavu,Pollachi,Coimbatore,Tamil Nadu,10.96,77.07,Agricultural,Borewell,450.0,2019-09-20,Patta-TN-67890\n"
                +
                "SY-MH-789-1C,8.50,Wardha,Wardha,Wardha,Maharashtra,20.74,78.61,Agricultural,Canal,300.0,2017-04-10,7/12-MH-11122\n";

        try (BufferedWriter w = new BufferedWriter(new FileWriter(LAND_DATA_FILE, false))) {
            w.write(landSample);
        }

        LOGGER.info("Sample data files created in data/ directory.");
    }

    // --- Helper utilities ---

    private String nextToken(StringTokenizer tok) {
        return tok.hasMoreTokens() ? tok.nextToken().trim() : "";
    }

    private double parseDouble(String val) {
        if (val == null || val.isBlank())
            return 0.0;
        return Double.parseDouble(val);
    }

    private String nullSafe(Object obj) {
        return obj == null ? "" : obj.toString();
    }
}
