package com.soilmanagement.filehandler;

import com.soilmanagement.domain.Soil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * SoilLandFileHandlerTest — tests FileReader/Writer/BufferedReader CSV I/O.
 */
class SoilLandFileHandlerTest {

    private SoilLandFileHandler handler;

    @BeforeEach
    void setUp() {
        handler = new SoilLandFileHandler();
    }

    @Test
    void testWriteAndReadSoilRecords(@TempDir Path tempDir) throws IOException {
        List<Soil> soils = List.of(
            Soil.builder()
                .soilSampleCode("TEST-FILE-001")
                .soilType("Alluvial")
                .phLevel(6.8)
                .organicCarbonPercent(1.85)
                .nitrogenContentKgPerHa(180.0)
                .phosphorusContentKgPerHa(22.0)
                .potassiumContentKgPerHa(145.0)
                .waterHoldingCapacityPercent(42.0)
                .electricalConductivityDsPerM(0.45)
                .texture("Loamy")
                .bulkDensityGPerCm3(1.35)
                .sampleCollectionDate(LocalDate.of(2024, 1, 15))
                .collectionLocation("Test District")
                .build()
        );

        String filePath = tempDir.resolve("test_soil.csv").toString();
        handler.writeSoilRecords(soils, filePath);
        List<Soil> read = handler.readSoilRecords(filePath);

        assertEquals(1, read.size());
        assertEquals("TEST-FILE-001", read.get(0).getSoilSampleCode());
        assertEquals("Alluvial",      read.get(0).getSoilType());
        assertEquals(6.8,             read.get(0).getPhLevel(), 0.001);
    }

    @Test
    void testReadNonExistentFileThrowsIOException() {
        assertThrows(IOException.class,
            () -> handler.readSoilRecords("/nonexistent/path/file.csv"));
    }

    @Test
    void testAppendToReportLog(@TempDir Path tempDir) throws IOException {
        // Override the log path by calling directly
        // (In production, the path would be configurable)
        new java.io.File("data").mkdirs();
        assertDoesNotThrow(() -> handler.appendToReportLog("Test log entry"));
    }
}
