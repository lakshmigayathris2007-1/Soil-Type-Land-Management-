package com.soilmanagement.domain;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "lands")
public class Land implements Analyzable {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true, length = 30)
    private String surveyNumber;
    @Column(nullable = false)
    private Double areaInAcres;
    @Column(nullable = false, length = 100)
    private String village;
    @Column(nullable = false, length = 100)
    private String taluk;
    @Column(nullable = false, length = 100)
    private String district;
    @Column(nullable = false, length = 100)
    private String state;
    @Column
    private Double latitudeCoordinate;
    @Column
    private Double longitudeCoordinate;
    @Column(length = 50)
    private String landClassification;
    @Column(length = 50)
    private String irrigationSourceType;
    @Column
    private Double elevationMeters;
    @Column
    private LocalDate registrationDate;
    @Column(length = 200)
    private String ownershipDocument;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "farmer_id")
    private Farmer owner;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "soil_id")
    private Soil associatedSoil;

    public Land() {}

    @Override
    public double calculateIndex() {
        double score = 0;
        score += Math.min(30, areaInAcres * 3);
        if (irrigationSourceType != null) {
            score += switch (irrigationSourceType.toUpperCase()) {
                case "CANAL" -> 30; case "BOREWELL" -> 25;
                case "DRIP"  -> 35; case "RAINWATER" -> 15;
                default      -> 10;
            };
        }
        if (elevationMeters != null)
            score += (elevationMeters >= 100 && elevationMeters <= 500) ? 20 : 10;
        return Math.min(100, Math.round(score * 10.0) / 10.0);
    }

    @Override
    public String analyzeCondition() {
        double idx = calculateIndex();
        if (idx >= 75) return "HIGH_PRODUCTIVITY";
        if (idx >= 50) return "MEDIUM_PRODUCTIVITY";
        return "LOW_PRODUCTIVITY";
    }

    @Override
    public boolean meetsStandard(String standardCode) {
        if ("MINIMUM_VIABLE".equals(standardCode)) return areaInAcres >= 0.5;
        if ("IRRIGATION_READY".equals(standardCode))
            return irrigationSourceType != null && !irrigationSourceType.isBlank();
        return false;
    }

    public String getFullAddress() {
        return village + ", " + taluk + ", " + district + ", " + state;
    }

    public double estimateDailyWaterRequirementLitres(double cropWaterNeedMmPerDay) {
        return areaInAcres * 4046.86 * (cropWaterNeedMmPerDay / 1000.0);
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getSurveyNumber() { return surveyNumber; }
    public void setSurveyNumber(String v) { this.surveyNumber = v; }
    public Double getAreaInAcres() { return areaInAcres; }
    public void setAreaInAcres(Double v) { this.areaInAcres = v; }
    public String getVillage() { return village; }
    public void setVillage(String v) { this.village = v; }
    public String getTaluk() { return taluk; }
    public void setTaluk(String v) { this.taluk = v; }
    public String getDistrict() { return district; }
    public void setDistrict(String v) { this.district = v; }
    public String getState() { return state; }
    public void setState(String v) { this.state = v; }
    public Double getLatitudeCoordinate() { return latitudeCoordinate; }
    public void setLatitudeCoordinate(Double v) { this.latitudeCoordinate = v; }
    public Double getLongitudeCoordinate() { return longitudeCoordinate; }
    public void setLongitudeCoordinate(Double v) { this.longitudeCoordinate = v; }
    public String getLandClassification() { return landClassification; }
    public void setLandClassification(String v) { this.landClassification = v; }
    public String getIrrigationSourceType() { return irrigationSourceType; }
    public void setIrrigationSourceType(String v) { this.irrigationSourceType = v; }
    public Double getElevationMeters() { return elevationMeters; }
    public void setElevationMeters(Double v) { this.elevationMeters = v; }
    public LocalDate getRegistrationDate() { return registrationDate; }
    public void setRegistrationDate(LocalDate v) { this.registrationDate = v; }
    public String getOwnershipDocument() { return ownershipDocument; }
    public void setOwnershipDocument(String v) { this.ownershipDocument = v; }
    public Farmer getOwner() { return owner; }
    public void setOwner(Farmer owner) { this.owner = owner; }
    public Soil getAssociatedSoil() { return associatedSoil; }
    public void setAssociatedSoil(Soil associatedSoil) { this.associatedSoil = associatedSoil; }
}
