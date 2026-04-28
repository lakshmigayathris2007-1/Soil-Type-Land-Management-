package com.soilmanagement.domain;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Entity
@Table(name = "land_usages")
public class LandUsage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 30)
    private String usageRecordCode;

    @Column(nullable = false)
    private LocalDate usageStartDate;

    @Column
    private LocalDate usageEndDate;

    @Column(nullable = false, length = 50)
    private String usageType;

    @Column(length = 100)
    private String cropGrown;

    @Column
    private Double areaUnderCultivationAcres;

    @Column
    private Double actualYieldTons;

    @Column
    private Double expectedYieldTons;

    @Column
    private Double inputCostRupees;

    @Column
    private Double revenueEarnedRupees;

    @Column(length = 200)
    private String challenges;

    @Column(length = 50)
    private String season;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "land_id")
    private Land land;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "crop_id")
    private Crop crop;

    public LandUsage() {}

    public double calculateProfitOrLoss() {
        return (revenueEarnedRupees != null ? revenueEarnedRupees : 0)
             - (inputCostRupees     != null ? inputCostRupees     : 0);
    }

    public double getYieldEfficiencyPercent() {
        if (expectedYieldTons == null || expectedYieldTons == 0 || actualYieldTons == null) return 0;
        return (actualYieldTons / expectedYieldTons) * 100.0;
    }

    public boolean isCurrentlyActive() {
        LocalDate today = LocalDate.now();
        return !today.isBefore(usageStartDate)
                && (usageEndDate == null || !today.isAfter(usageEndDate));
    }

    public long getDurationDays() {
        LocalDate end = usageEndDate != null ? usageEndDate : LocalDate.now();
        return ChronoUnit.DAYS.between(usageStartDate, end);
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getUsageRecordCode() { return usageRecordCode; }
    public void setUsageRecordCode(String v) { this.usageRecordCode = v; }
    public LocalDate getUsageStartDate() { return usageStartDate; }
    public void setUsageStartDate(LocalDate v) { this.usageStartDate = v; }
    public LocalDate getUsageEndDate() { return usageEndDate; }
    public void setUsageEndDate(LocalDate v) { this.usageEndDate = v; }
    public String getUsageType() { return usageType; }
    public void setUsageType(String v) { this.usageType = v; }
    public String getCropGrown() { return cropGrown; }
    public void setCropGrown(String v) { this.cropGrown = v; }
    public Double getAreaUnderCultivationAcres() { return areaUnderCultivationAcres; }
    public void setAreaUnderCultivationAcres(Double v) { this.areaUnderCultivationAcres = v; }
    public Double getActualYieldTons() { return actualYieldTons; }
    public void setActualYieldTons(Double v) { this.actualYieldTons = v; }
    public Double getExpectedYieldTons() { return expectedYieldTons; }
    public void setExpectedYieldTons(Double v) { this.expectedYieldTons = v; }
    public Double getInputCostRupees() { return inputCostRupees; }
    public void setInputCostRupees(Double v) { this.inputCostRupees = v; }
    public Double getRevenueEarnedRupees() { return revenueEarnedRupees; }
    public void setRevenueEarnedRupees(Double v) { this.revenueEarnedRupees = v; }
    public String getChallenges() { return challenges; }
    public void setChallenges(String v) { this.challenges = v; }
    public String getSeason() { return season; }
    public void setSeason(String v) { this.season = v; }
    public Land getLand() { return land; }
    public void setLand(Land v) { this.land = v; }
    public Crop getCrop() { return crop; }
    public void setCrop(Crop v) { this.crop = v; }
}
