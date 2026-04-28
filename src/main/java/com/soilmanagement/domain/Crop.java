package com.soilmanagement.domain;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "crops")
public class Crop {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String cropName;

    @Column(nullable = false, length = 50)
    private String cropCategory;

    @Column(length = 100)
    private String scientificName;

    @Column(nullable = false)
    private Integer growthDurationDays;

    @Column(nullable = false)
    private Double minPhRequired;

    @Column(nullable = false)
    private Double maxPhRequired;

    @Column
    private Double waterRequirementMmPerSeason;

    @Column
    private Double expectedYieldTonsPerAcre;

    @Column(length = 50)
    private String preferredSoilTexture;

    @Column
    private Double minTemperatureCelsius;

    @Column
    private Double maxTemperatureCelsius;

    @Column
    private LocalDate sowingSeasonStart;

    @Column
    private LocalDate sowingSeasonEnd;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "land_id")
    private Land cultivatedOn;

    public Crop() {}

    public boolean isSoilCompatible(Soil soil) {
        if (soil == null) return false;
        boolean phOk = soil.getPhLevel() >= minPhRequired && soil.getPhLevel() <= maxPhRequired;
        boolean textureOk = preferredSoilTexture == null
                || preferredSoilTexture.equalsIgnoreCase(soil.getTexture());
        return phOk && textureOk;
    }

    public boolean isInSowingWindow() {
        LocalDate today = LocalDate.now();
        return sowingSeasonStart != null && sowingSeasonEnd != null
                && !today.isBefore(sowingSeasonStart) && !today.isAfter(sowingSeasonEnd);
    }

    public boolean isInSowingWindow(LocalDate checkDate) {
        return sowingSeasonStart != null && sowingSeasonEnd != null
                && !checkDate.isBefore(sowingSeasonStart) && !checkDate.isAfter(sowingSeasonEnd);
    }

    public double estimateRevenueInRupees(double marketPricePerTon) {
        if (expectedYieldTonsPerAcre == null) return 0;
        return expectedYieldTonsPerAcre * marketPricePerTon;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getCropName() { return cropName; }
    public void setCropName(String v) { this.cropName = v; }
    public String getCropCategory() { return cropCategory; }
    public void setCropCategory(String v) { this.cropCategory = v; }
    public String getScientificName() { return scientificName; }
    public void setScientificName(String v) { this.scientificName = v; }
    public Integer getGrowthDurationDays() { return growthDurationDays; }
    public void setGrowthDurationDays(Integer v) { this.growthDurationDays = v; }
    public Double getMinPhRequired() { return minPhRequired; }
    public void setMinPhRequired(Double v) { this.minPhRequired = v; }
    public Double getMaxPhRequired() { return maxPhRequired; }
    public void setMaxPhRequired(Double v) { this.maxPhRequired = v; }
    public Double getWaterRequirementMmPerSeason() { return waterRequirementMmPerSeason; }
    public void setWaterRequirementMmPerSeason(Double v) { this.waterRequirementMmPerSeason = v; }
    public Double getExpectedYieldTonsPerAcre() { return expectedYieldTonsPerAcre; }
    public void setExpectedYieldTonsPerAcre(Double v) { this.expectedYieldTonsPerAcre = v; }
    public String getPreferredSoilTexture() { return preferredSoilTexture; }
    public void setPreferredSoilTexture(String v) { this.preferredSoilTexture = v; }
    public Double getMinTemperatureCelsius() { return minTemperatureCelsius; }
    public void setMinTemperatureCelsius(Double v) { this.minTemperatureCelsius = v; }
    public Double getMaxTemperatureCelsius() { return maxTemperatureCelsius; }
    public void setMaxTemperatureCelsius(Double v) { this.maxTemperatureCelsius = v; }
    public LocalDate getSowingSeasonStart() { return sowingSeasonStart; }
    public void setSowingSeasonStart(LocalDate v) { this.sowingSeasonStart = v; }
    public LocalDate getSowingSeasonEnd() { return sowingSeasonEnd; }
    public void setSowingSeasonEnd(LocalDate v) { this.sowingSeasonEnd = v; }
    public Land getCultivatedOn() { return cultivatedOn; }
    public void setCultivatedOn(Land v) { this.cultivatedOn = v; }

    @Override
    public String toString() {
        return "Crop{name='" + cropName + "', category='" + cropCategory
                + "', duration=" + growthDurationDays + " days}";
    }
}
