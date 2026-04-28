package com.soilmanagement.thread;

import org.springframework.stereotype.Component;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.util.logging.Logger;

/**
 * ThreadManager
 * Starts and stops background threads during application lifecycle.
 * Uses Spring's @PostConstruct / @PreDestroy for lifecycle hooks.
 */
@Component
public class ThreadManager {

    private static final Logger LOGGER = Logger.getLogger(ThreadManager.class.getName());

    private final SensorDataCollectionThread sensorThread;
    private final ReportGenerationThread     reportThread;

    private Thread sensorWorker;
    private Thread reportWorker;

    public ThreadManager(SensorDataCollectionThread sensorThread,
                         ReportGenerationThread reportThread) {
        this.sensorThread = sensorThread;
        this.reportThread = reportThread;
    }

    @PostConstruct
    public void startThreads() {
        // Configure faster intervals for demo (2s sensor, 10s report)
        sensorThread.setPollIntervalMs(2000);
        reportThread.setReportIntervalMs(10000);

        sensorWorker = new Thread(sensorThread, "SensorDataCollectionThread");
        reportWorker = new Thread(reportThread, "ReportGenerationThread");

        sensorWorker.setDaemon(true); // daemon threads stop when JVM exits
        reportWorker.setDaemon(true);

        sensorWorker.start();
        reportWorker.start();

        LOGGER.info("[ThreadManager] Background threads started.");
    }

    @PreDestroy
    public void stopThreads() {
        sensorThread.stop();
        reportThread.stop();

        if (sensorWorker != null) sensorWorker.interrupt();
        if (reportWorker != null) reportWorker.interrupt();

        LOGGER.info("[ThreadManager] Background threads stopped.");
    }
}
