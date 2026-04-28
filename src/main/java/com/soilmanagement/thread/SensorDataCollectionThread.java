package com.soilmanagement.thread;

import com.soilmanagement.domain.SensorData;
import com.soilmanagement.filehandler.SoilLandFileHandler;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Thread 1: SensorDataCollectionThread
 * Simulates continuous IoT sensor data ingestion in a background thread.
 *
 * Demonstrates:
 * - Runnable interface implementation
 * - synchronized block for shared resource access
 * - Thread sleep to simulate periodic sensor polling
 */
@Component
public class SensorDataCollectionThread implements Runnable {

    private static final Logger LOGGER = Logger.getLogger(SensorDataCollectionThread.class.getName());

    // Shared buffer — access must be synchronized across threads
    private final List<SensorData> sensorBuffer = new ArrayList<>();

    private final SoilLandFileHandler fileHandler;
    private volatile boolean running = false; // volatile ensures visibility across threads
    private int pollIntervalMs = 5000;        // 5 seconds between polls

    private static final String[] SENSOR_IDS   = {"SENS-001", "SENS-002", "SENS-003"};
    private static final String[] SENSOR_TYPES = {"SOIL_MOISTURE", "SOIL_TEMP", "PH_PROBE"};

    public SensorDataCollectionThread(SoilLandFileHandler fileHandler) {
        this.fileHandler = fileHandler;
    }

    @Override
    public void run() {
        LOGGER.info("[SensorThread] Starting sensor data collection...");
        running = true;
        int cycle = 0;

        while (running) {
            try {
                // Simulate reading from multiple sensors
                for (int i = 0; i < SENSOR_IDS.length; i++) {
                    SensorData reading = simulateSensorReading(SENSOR_IDS[i], SENSOR_TYPES[i], cycle);

                    // synchronized block — only one thread modifies the buffer at a time
                    synchronized (sensorBuffer) {
                        sensorBuffer.add(reading);
                        LOGGER.info("[SensorThread] Captured: " + reading.getSensorId()
                                + " | " + reading.getSensorType()
                                + " | Value: " + reading.getPrimaryReadingValue()
                                + " " + reading.getPrimaryReadingUnit()
                                + " at " + reading.getCapturedAt());

                        // Flush buffer to log every 10 readings
                        if (sensorBuffer.size() >= 9) {
                            flushBufferToLog();
                        }
                    }
                }

                cycle++;
                Thread.sleep(pollIntervalMs);

            } catch (InterruptedException e) {
                LOGGER.warning("[SensorThread] Thread interrupted: " + e.getMessage());
                Thread.currentThread().interrupt(); // restore interrupted status
                running = false;
            }
        }
        LOGGER.info("[SensorThread] Sensor collection stopped.");
    }

    /**
     * Simulates a realistic sensor reading with slight variation per cycle.
     */
    private SensorData simulateSensorReading(String sensorId, String sensorType, int cycle) {
        double baseValue;
        String unit;

        switch (sensorType) {
            case "SOIL_MOISTURE" -> { baseValue = 38.5 + (cycle % 10) * 0.3; unit = "%"; }
            case "SOIL_TEMP"     -> { baseValue = 24.2 + (cycle % 5)  * 0.5; unit = "°C"; }
            case "PH_PROBE"      -> { baseValue = 6.7  + (cycle % 3)  * 0.1; unit = "pH"; }
            default              -> { baseValue = 0.0; unit = "unknown"; }
        }

        // Build raw CSV payload string using StringBuilder
        StringBuilder payload = new StringBuilder();
        payload.append(sensorId).append(",")
               .append(sensorType).append(",")
               .append(String.format("%.2f", baseValue)).append(",")
               .append(unit).append(",")
               .append("12.0").append(",V,")
               .append(String.format("%.1f", 90.0 - cycle * 0.1)).append(",%,")
               .append("-68");

        SensorData data = new SensorData();
        data.setSensorId(sensorId);
        data.setSensorType(sensorType);
        data.setCapturedAt(LocalDateTime.now());
        data.setPrimaryReadingValue(baseValue);
        data.setPrimaryReadingUnit(unit);
        data.setBatteryLevelPercent(90.0 - cycle * 0.1);
        data.setSignalStrengthDbm(-68.0);
        data.setIsCalibrated(true);
        data.setRawPayload(payload.toString());
        return data;
    }

    /**
     * Flush the in-memory buffer to a log file.
     * Called within synchronized block — thread-safe.
     */
    private void flushBufferToLog() {
        StringBuilder logEntry = new StringBuilder();
        logEntry.append("=== SENSOR FLUSH @ ").append(LocalDateTime.now()).append(" ===\n");
        for (SensorData sd : sensorBuffer) {
            logEntry.append(sd.getSensorId()).append("|")
                    .append(sd.getSensorType()).append("|")
                    .append(sd.getPrimaryReadingValue()).append(" ")
                    .append(sd.getPrimaryReadingUnit()).append("|")
                    .append(sd.getCapturedAt()).append("\n");
        }
        fileHandler.appendToReportLog(logEntry.toString());
        sensorBuffer.clear();
        LOGGER.info("[SensorThread] Buffer flushed to log.");
    }

    /**
     * Thread-safe accessor for external consumers to read collected data.
     */
    public synchronized List<SensorData> drainBuffer() {
        List<SensorData> snapshot = new ArrayList<>(sensorBuffer);
        sensorBuffer.clear();
        return snapshot;
    }

    public void stop()  { this.running = false; }
    public boolean isRunning() { return running; }
    public void setPollIntervalMs(int ms) { this.pollIntervalMs = ms; }
}
