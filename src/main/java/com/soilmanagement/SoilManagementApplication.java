package com.soilmanagement;

import com.soilmanagement.filehandler.SoilLandFileHandler;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.logging.Logger;

/**
 * SoilManagementApplication
 * Spring Boot entry point.
 *
 * @SpringBootApplication combines:
 *   - @Configuration (bean source)
 *   - @EnableAutoConfiguration (Spring Boot auto-config)
 *   - @ComponentScan (scans com.soilmanagement.*)
 */
@SpringBootApplication
public class SoilManagementApplication {

    private static final Logger LOGGER =
        Logger.getLogger(SoilManagementApplication.class.getName());

    public static void main(String[] args) {
        SpringApplication.run(SoilManagementApplication.class, args);
        LOGGER.info("╔═══════════════════════════════════════════════╗");
        LOGGER.info("║  Soil Type & Land Usage Management System     ║");
        LOGGER.info("║  Started — http://localhost:8080              ║");
        LOGGER.info("╚═══════════════════════════════════════════════╝");
    }

    /**
     * On startup, create sample CSV data files so the file-handling demo works.
     */
    @Bean
    CommandLineRunner initSampleFiles(SoilLandFileHandler fileHandler) {
        return args -> {
            try {
                fileHandler.createSampleDataFiles();
                LOGGER.info("Sample data files initialised in data/ directory.");
            } catch (Exception e) {
                LOGGER.warning("Could not create sample files: " + e.getMessage());
            }
        };
    }
}
