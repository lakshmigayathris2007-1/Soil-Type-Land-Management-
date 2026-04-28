package com.soilmanagement.domain;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "irrigations")
public class Irrigation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 30)
    private String irrigationEventCode;

    @Column(nullable = false, length = 50)
    private String irrigationMethod;

    @Column(nullable = false)
    private LocalDate irrigationDate;

    @Column(nullable = false)
    private LocalTime startTime;

    @Column(nullable = false)
    private LocalTime endTime;

    @Column(nullable = false)
    private Double waterVolumeUsedLitres;

    @Column
    private Double flowRateLitresPerHour;

    @Column
    private Double soilMoistureBeforePercent;

    @Column
    private Double soilMoistureAfterPercent;

    @Column(length = 50)
    private String waterSource;

    @Column
    private Double electricityConsumedKwh;

    @Column
    private Double costPerIrrigationRupees;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "land_id")
    private Land irrigatedLand;

    public Irrigation() {}

    public double calculateEfficiencyPercent() {
        if (soilMoistureBeforePercent == null || soilMoistureAfterPercent == null
                || waterVolumeUsedLitres == null || waterVolumeUsedLitres == 0) return 0;
        double gain = soilMoistureAfterPercent - soilMoistureBeforePercent;
        return Math.min(100, (gain / waterVolumeUsedLitres) * 10000);
    }

    public long getDurationMinutes() {
        return java.time.Duration.between(startTime, endTime).toMinutes();
    }

    public boolean isWithinBudget(double budgetRupees) {
        return costPerIrrigationRupees != null && costPerIrrigationRupees <= budgetRupees;
    }

    public boolean isWithinBudget(double budgetRupees, double electricityRatePerKwh) {
        double total = (costPerIrrigationRupees != null ? costPerIrrigationRupees : 0)
                     + (electricityConsumedKwh  != null ? electricityConsumedKwh * electricityRatePerKwh : 0);
        return total <= budgetRupees;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getIrrigationEventCode() { return irrigationEventCode; }
    public void setIrrigationEventCode(String v) { this.irrigationEventCode = v; }
    public String getIrrigationMethod() { return irrigationMethod; }
    public void setIrrigationMethod(String v) { this.irrigationMethod = v; }
    public LocalDate getIrrigationDate() { return irrigationDate; }
    public void setIrrigationDate(LocalDate v) { this.irrigationDate = v; }
    public LocalTime getStartTime() { return startTime; }
    public void setStartTime(LocalTime v) { this.startTime = v; }
    public LocalTime getEndTime() { return endTime; }
    public void setEndTime(LocalTime v) { this.endTime = v; }
    public Double getWaterVolumeUsedLitres() { return waterVolumeUsedLitres; }
    public void setWaterVolumeUsedLitres(Double v) { this.waterVolumeUsedLitres = v; }
    public Double getFlowRateLitresPerHour() { return flowRateLitresPerHour; }
    public void setFlowRateLitresPerHour(Double v) { this.flowRateLitresPerHour = v; }
    public Double getSoilMoistureBeforePercent() { return soilMoistureBeforePercent; }
    public void setSoilMoistureBeforePercent(Double v) { this.soilMoistureBeforePercent = v; }
    public Double getSoilMoistureAfterPercent() { return soilMoistureAfterPercent; }
    public void setSoilMoistureAfterPercent(Double v) { this.soilMoistureAfterPercent = v; }
    public String getWaterSource() { return waterSource; }
    public void setWaterSource(String v) { this.waterSource = v; }
    public Double getElectricityConsumedKwh() { return electricityConsumedKwh; }
    public void setElectricityConsumedKwh(Double v) { this.electricityConsumedKwh = v; }
    public Double getCostPerIrrigationRupees() { return costPerIrrigationRupees; }
    public void setCostPerIrrigationRupees(Double v) { this.costPerIrrigationRupees = v; }
    public Land getIrrigatedLand() { return irrigatedLand; }
    public void setIrrigatedLand(Land v) { this.irrigatedLand = v; }
}
