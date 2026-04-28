package com.soilmanagement.domain;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "soils")
public class Soil implements Analyzable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true, length = 30)
    private String soilSampleCode;
    @Column(nullable = false, length = 50)
    private String soilType;
    @Column(nullable = false)
    private Double phLevel;
    @Column(nullable = false)
    private Double organicCarbonPercent;
    @Column(nullable = false)
    private Double nitrogenContentKgPerHa;
    @Column(nullable = false)
    private Double phosphorusContentKgPerHa;
    @Column(nullable = false)
    private Double potassiumContentKgPerHa;
    @Column
    private Double waterHoldingCapacityPercent;
    @Column
    private Double electricalConductivityDsPerM;
    @Column(length = 30)
    private String texture;
    @Column
    private Double bulkDensityGPerCm3;
    @Column
    private LocalDate sampleCollectionDate;
    @Column(length = 200)
    private String collectionLocation;

    public Soil() {}

    public Soil(String soilSampleCode, String soilType, Double phLevel,
                Double organicCarbonPercent, Double nitrogenContentKgPerHa,
                Double phosphorusContentKgPerHa, Double potassiumContentKgPerHa,
                Double waterHoldingCapacityPercent, Double electricalConductivityDsPerM,
                String texture, Double bulkDensityGPerCm3,
                LocalDate sampleCollectionDate, String collectionLocation) {
        this.soilSampleCode = soilSampleCode;
        this.soilType = soilType;
        this.phLevel = phLevel;
        this.organicCarbonPercent = organicCarbonPercent;
        this.nitrogenContentKgPerHa = nitrogenContentKgPerHa;
        this.phosphorusContentKgPerHa = phosphorusContentKgPerHa;
        this.potassiumContentKgPerHa = potassiumContentKgPerHa;
        this.waterHoldingCapacityPercent = waterHoldingCapacityPercent;
        this.electricalConductivityDsPerM = electricalConductivityDsPerM;
        this.texture = texture;
        this.bulkDensityGPerCm3 = bulkDensityGPerCm3;
        this.sampleCollectionDate = sampleCollectionDate;
        this.collectionLocation = collectionLocation;
    }

    // Builder pattern (replaces @Builder)
    public static SoilBuilder builder() { return new SoilBuilder(); }

    public static class SoilBuilder {
        private String soilSampleCode, soilType, texture, collectionLocation;
        private Double phLevel, organicCarbonPercent, nitrogenContentKgPerHa;
        private Double phosphorusContentKgPerHa, potassiumContentKgPerHa;
        private Double waterHoldingCapacityPercent, electricalConductivityDsPerM, bulkDensityGPerCm3;
        private LocalDate sampleCollectionDate;

        public SoilBuilder soilSampleCode(String v) { this.soilSampleCode = v; return this; }
        public SoilBuilder soilType(String v) { this.soilType = v; return this; }
        public SoilBuilder phLevel(Double v) { this.phLevel = v; return this; }
        public SoilBuilder organicCarbonPercent(Double v) { this.organicCarbonPercent = v; return this; }
        public SoilBuilder nitrogenContentKgPerHa(Double v) { this.nitrogenContentKgPerHa = v; return this; }
        public SoilBuilder phosphorusContentKgPerHa(Double v) { this.phosphorusContentKgPerHa = v; return this; }
        public SoilBuilder potassiumContentKgPerHa(Double v) { this.potassiumContentKgPerHa = v; return this; }
        public SoilBuilder waterHoldingCapacityPercent(Double v) { this.waterHoldingCapacityPercent = v; return this; }
        public SoilBuilder electricalConductivityDsPerM(Double v) { this.electricalConductivityDsPerM = v; return this; }
        public SoilBuilder texture(String v) { this.texture = v; return this; }
        public SoilBuilder bulkDensityGPerCm3(Double v) { this.bulkDensityGPerCm3 = v; return this; }
        public SoilBuilder sampleCollectionDate(LocalDate v) { this.sampleCollectionDate = v; return this; }
        public SoilBuilder collectionLocation(String v) { this.collectionLocation = v; return this; }
        public Soil build() {
            return new Soil(soilSampleCode, soilType, phLevel, organicCarbonPercent,
                    nitrogenContentKgPerHa, phosphorusContentKgPerHa, potassiumContentKgPerHa,
                    waterHoldingCapacityPercent, electricalConductivityDsPerM,
                    texture, bulkDensityGPerCm3, sampleCollectionDate, collectionLocation);
        }
    }

    @Override
    public double calculateIndex() {
        double phScore = (phLevel >= 6.0 && phLevel <= 7.5) ? 25.0
                : Math.max(0, 25.0 - Math.abs(phLevel - 6.75) * 10);
        double ocScore = Math.min(25.0, organicCarbonPercent * 12.5);
        double nScore  = Math.min(20.0, nitrogenContentKgPerHa / 10.0);
        double pScore  = Math.min(15.0, phosphorusContentKgPerHa / 5.0);
        double kScore  = Math.min(15.0, potassiumContentKgPerHa / 15.0);
        return Math.round((phScore + ocScore + nScore + pScore + kScore) * 10.0) / 10.0;
    }

    @Override
    public String analyzeCondition() {
        double index = calculateIndex();
        if (index >= 80) return "EXCELLENT";
        else if (index >= 60) return "GOOD";
        else if (index >= 40) return "MODERATE";
        else return "POOR";
    }

    @Override
    public boolean meetsStandard(String standardCode) {
        if ("ORGANIC_FARMING".equals(standardCode))
            return organicCarbonPercent >= 1.5 && electricalConductivityDsPerM < 2.0;
        if ("BASIC_AGRICULTURE".equals(standardCode))
            return phLevel >= 5.5 && phLevel <= 8.5;
        return false;
    }

    public String recommendAmendment() {
        if (phLevel < 5.5) return "Apply agricultural lime to raise pH";
        if (phLevel > 8.5) return "Apply elemental sulphur or gypsum to lower pH";
        if (organicCarbonPercent < 0.5) return "Add farm yard manure or compost";
        return "Soil is within acceptable range — monitor regularly";
    }

    public boolean isDeficient(String nutrient) {
        return switch (nutrient.toUpperCase()) {
            case "NITROGEN"   -> nitrogenContentKgPerHa < 140;
            case "PHOSPHORUS" -> phosphorusContentKgPerHa < 11;
            case "POTASSIUM"  -> potassiumContentKgPerHa < 110;
            default           -> false;
        };
    }

    public boolean isDeficient(String nutrient, double threshold) {
        return switch (nutrient.toUpperCase()) {
            case "NITROGEN"   -> nitrogenContentKgPerHa < threshold;
            case "PHOSPHORUS" -> phosphorusContentKgPerHa < threshold;
            case "POTASSIUM"  -> potassiumContentKgPerHa < threshold;
            default           -> false;
        };
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getSoilSampleCode() { return soilSampleCode; }
    public void setSoilSampleCode(String v) { this.soilSampleCode = v; }
    public String getSoilType() { return soilType; }
    public void setSoilType(String v) { this.soilType = v; }
    public Double getPhLevel() { return phLevel; }
    public void setPhLevel(Double v) { this.phLevel = v; }
    public Double getOrganicCarbonPercent() { return organicCarbonPercent; }
    public void setOrganicCarbonPercent(Double v) { this.organicCarbonPercent = v; }
    public Double getNitrogenContentKgPerHa() { return nitrogenContentKgPerHa; }
    public void setNitrogenContentKgPerHa(Double v) { this.nitrogenContentKgPerHa = v; }
    public Double getPhosphorusContentKgPerHa() { return phosphorusContentKgPerHa; }
    public void setPhosphorusContentKgPerHa(Double v) { this.phosphorusContentKgPerHa = v; }
    public Double getPotassiumContentKgPerHa() { return potassiumContentKgPerHa; }
    public void setPotassiumContentKgPerHa(Double v) { this.potassiumContentKgPerHa = v; }
    public Double getWaterHoldingCapacityPercent() { return waterHoldingCapacityPercent; }
    public void setWaterHoldingCapacityPercent(Double v) { this.waterHoldingCapacityPercent = v; }
    public Double getElectricalConductivityDsPerM() { return electricalConductivityDsPerM; }
    public void setElectricalConductivityDsPerM(Double v) { this.electricalConductivityDsPerM = v; }
    public String getTexture() { return texture; }
    public void setTexture(String v) { this.texture = v; }
    public Double getBulkDensityGPerCm3() { return bulkDensityGPerCm3; }
    public void setBulkDensityGPerCm3(Double v) { this.bulkDensityGPerCm3 = v; }
    public LocalDate getSampleCollectionDate() { return sampleCollectionDate; }
    public void setSampleCollectionDate(LocalDate v) { this.sampleCollectionDate = v; }
    public String getCollectionLocation() { return collectionLocation; }
    public void setCollectionLocation(String v) { this.collectionLocation = v; }

    @Override
    public String toString() {
        return "Soil{code='" + soilSampleCode + "', type='" + soilType + "', pH=" + phLevel + "}";
    }
}
