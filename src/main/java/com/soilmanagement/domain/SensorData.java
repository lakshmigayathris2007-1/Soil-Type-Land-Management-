package com.soilmanagement.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

@Entity
@Table(name = "sensor_data")
public class SensorData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 30)
    private String sensorId;

    @Column(nullable = false, length = 50)
    private String sensorType;

    @Column(nullable = false)
    private LocalDateTime capturedAt;

    @Column(nullable = false)
    private Double primaryReadingValue;

    @Column(length = 20)
    private String primaryReadingUnit;

    @Column
    private Double secondaryReadingValue;

    @Column(length = 20)
    private String secondaryReadingUnit;

    @Column
    private Double batteryLevelPercent;

    @Column
    private Double signalStrengthDbm;

    @Column
    private Boolean isCalibrated;

    @Column(length = 500)
    private String rawPayload;

    @Column(length = 100)
    private String deployedAtGpsCoords;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "land_id")
    private Land deployedOnLand;

    public SensorData() {}

    // StringTokenizer — parses CSV sensor payload
    public Map<String, String> parseRawPayload() {
        Map<String, String> fields = new HashMap<>();
        if (rawPayload == null || rawPayload.isBlank()) return fields;
        StringTokenizer tokenizer = new StringTokenizer(rawPayload, ",");
        String[] keys = {"sensorId", "sensorType", "primaryValue", "primaryUnit",
                         "secondaryValue", "secondaryUnit", "battery", "batteryUnit", "signal"};
        int idx = 0;
        while (tokenizer.hasMoreTokens() && idx < keys.length)
            fields.put(keys[idx++], tokenizer.nextToken().trim());
        return fields;
    }

    public void loadFromPayload() {
        Map<String, String> fields = parseRawPayload();
        if (fields.containsKey("primaryValue")) {
            try { this.primaryReadingValue = Double.parseDouble(fields.get("primaryValue")); }
            catch (NumberFormatException e) { this.primaryReadingValue = -999.0; }
        }
        if (fields.containsKey("battery")) {
            try { this.batteryLevelPercent = Double.parseDouble(fields.get("battery")); }
            catch (NumberFormatException ignored) {}
        }
    }

    public boolean isAnomalousReading() {
        if ("SOIL_MOISTURE".equalsIgnoreCase(sensorType))
            return primaryReadingValue < 0 || primaryReadingValue > 100;
        if ("SOIL_TEMP".equalsIgnoreCase(sensorType))
            return primaryReadingValue < -10 || primaryReadingValue > 60;
        return false;
    }

    public String getBatteryStatus() {
        if (batteryLevelPercent == null) return "UNKNOWN";
        if (batteryLevelPercent >= 70)   return "GOOD";
        if (batteryLevelPercent >= 30)   return "LOW";
        return "CRITICAL";
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getSensorId() { return sensorId; }
    public void setSensorId(String v) { this.sensorId = v; }
    public String getSensorType() { return sensorType; }
    public void setSensorType(String v) { this.sensorType = v; }
    public LocalDateTime getCapturedAt() { return capturedAt; }
    public void setCapturedAt(LocalDateTime v) { this.capturedAt = v; }
    public Double getPrimaryReadingValue() { return primaryReadingValue; }
    public void setPrimaryReadingValue(Double v) { this.primaryReadingValue = v; }
    public String getPrimaryReadingUnit() { return primaryReadingUnit; }
    public void setPrimaryReadingUnit(String v) { this.primaryReadingUnit = v; }
    public Double getSecondaryReadingValue() { return secondaryReadingValue; }
    public void setSecondaryReadingValue(Double v) { this.secondaryReadingValue = v; }
    public String getSecondaryReadingUnit() { return secondaryReadingUnit; }
    public void setSecondaryReadingUnit(String v) { this.secondaryReadingUnit = v; }
    public Double getBatteryLevelPercent() { return batteryLevelPercent; }
    public void setBatteryLevelPercent(Double v) { this.batteryLevelPercent = v; }
    public Double getSignalStrengthDbm() { return signalStrengthDbm; }
    public void setSignalStrengthDbm(Double v) { this.signalStrengthDbm = v; }
    public Boolean getIsCalibrated() { return isCalibrated; }
    public void setIsCalibrated(Boolean v) { this.isCalibrated = v; }
    public String getRawPayload() { return rawPayload; }
    public void setRawPayload(String v) { this.rawPayload = v; }
    public String getDeployedAtGpsCoords() { return deployedAtGpsCoords; }
    public void setDeployedAtGpsCoords(String v) { this.deployedAtGpsCoords = v; }
    public Land getDeployedOnLand() { return deployedOnLand; }
    public void setDeployedOnLand(Land v) { this.deployedOnLand = v; }
}
