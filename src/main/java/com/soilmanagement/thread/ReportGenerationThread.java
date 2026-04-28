package com.soilmanagement.thread;

import com.soilmanagement.domain.Soil;
import com.soilmanagement.domain.Land;
import com.soilmanagement.filehandler.SoilLandFileHandler;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.logging.Logger;

/**
 * Thread 2: ReportGenerationThread
 * Asynchronously generates consolidated soil/land reports at scheduled intervals.
 *
 * Demonstrates:
 * - Thread-based asynchronous processing
 * - synchronized methods for shared state
 * - Iterator for safe traversal of pending report queue
 * - Comparator for sorting soils by fertility index
 * - StringBuffer for thread-safe report building
 */
@Component
public class ReportGenerationThread implements Runnable {

    private static final Logger LOGGER = Logger.getLogger(ReportGenerationThread.class.getName());

    private final SoilLandFileHandler fileHandler;

    // Pending report requests — shared between main thread and report thread
    private final List<Soil>  pendingSoils = new ArrayList<>();
    private final List<Land>  pendingLands = new ArrayList<>();

    private volatile boolean running = false;
    private int reportIntervalMs = 15000; // generate report every 15 seconds

    public ReportGenerationThread(SoilLandFileHandler fileHandler) {
        this.fileHandler = fileHandler;
    }

    @Override
    public void run() {
        LOGGER.info("[ReportThread] Starting report generation thread...");
        running = true;

        while (running) {
            try {
                Thread.sleep(reportIntervalMs);
                generateConsolidatedReport();
            } catch (InterruptedException e) {
                LOGGER.warning("[ReportThread] Interrupted: " + e.getMessage());
                Thread.currentThread().interrupt();
                running = false;
            }
        }
        LOGGER.info("[ReportThread] Report generation thread stopped.");
    }

    /**
     * Generate a consolidated report from pending soil and land records.
     * synchronized — prevents race conditions if addSoilForReport() is called concurrently.
     */
    public synchronized void generateConsolidatedReport() {
        if (pendingSoils.isEmpty() && pendingLands.isEmpty()) {
            LOGGER.info("[ReportThread] No pending data for report.");
            return;
        }

        // StringBuffer — thread-safe alternative to StringBuilder for concurrent contexts
        StringBuffer report = new StringBuffer();
        report.append("\n╔══════════════════════════════════════════╗\n");
        report.append("║   CONSOLIDATED SOIL & LAND REPORT        ║\n");
        report.append("║   Generated: ").append(LocalDateTime.now()).append(" ║\n");
        report.append("╚══════════════════════════════════════════╝\n");

        // Sort soils by fertility index descending — Comparator usage
        List<Soil> sortedSoils = new ArrayList<>(pendingSoils);
        sortedSoils.sort(Comparator.comparingDouble(Soil::calculateIndex).reversed());

        report.append("\n--- SOIL RECORDS (sorted by fertility index) ---\n");

        // Iterator pattern — explicit traversal of the sorted list
        Iterator<Soil> soilIterator = sortedSoils.iterator();
        int rank = 1;
        while (soilIterator.hasNext()) {
            Soil s = soilIterator.next();
            report.append(String.format("  #%d  %-20s | Type: %-12s | pH: %.1f | Index: %.1f/100 | Status: %s%n",
                rank++, s.getSoilSampleCode(), s.getSoilType(),
                s.getPhLevel(), s.calculateIndex(), s.analyzeCondition()));
        }

        // Sort lands by area descending — Comparator
        List<Land> sortedLands = new ArrayList<>(pendingLands);
        sortedLands.sort(Comparator.comparingDouble(Land::getAreaInAcres).reversed());

        report.append("\n--- LAND RECORDS (sorted by area) ---\n");
        Iterator<Land> landIterator = sortedLands.iterator();
        while (landIterator.hasNext()) {
            Land l = landIterator.next();
            report.append(String.format("  Survey: %-15s | Area: %6.2f acres | Location: %-30s | Productivity: %s%n",
                l.getSurveyNumber(), l.getAreaInAcres(),
                l.getFullAddress(), l.analyzeCondition()));
        }

        report.append("\n--- SUMMARY ---\n");
        report.append("  Total soils processed : ").append(pendingSoils.size()).append("\n");
        report.append("  Total lands processed : ").append(pendingLands.size()).append("\n");

        // Average fertility using stream
        double avgFertility = sortedSoils.stream()
            .mapToDouble(Soil::calculateIndex).average().orElse(0);
        report.append(String.format("  Average fertility index: %.2f/100%n", avgFertility));
        report.append("═".repeat(60)).append("\n");

        // Write report to log file
        fileHandler.appendToReportLog(report.toString());
        LOGGER.info("[ReportThread] Report generated: "
            + pendingSoils.size() + " soils, " + pendingLands.size() + " lands.");

        // Clear processed items
        pendingSoils.clear();
        pendingLands.clear();
    }

    /** Thread-safe method to queue a soil record for reporting */
    public synchronized void addSoilForReport(Soil soil) {
        if (soil != null) pendingSoils.add(soil);
    }

    /** Thread-safe method to queue a land record for reporting */
    public synchronized void addLandForReport(Land land) {
        if (land != null) pendingLands.add(land);
    }

    public void stop() { this.running = false; }
    public boolean isRunning() { return running; }
    public void setReportIntervalMs(int ms) { this.reportIntervalMs = ms; }
}
