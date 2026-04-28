package com.soilmanagement.domain;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@DiscriminatorValue("FARMER")
public class Farmer extends User implements Reportable {

    @Column(length = 30)
    private String farmerRegistrationId;
    @Column(nullable = false)
    private Double totalLandHolding;
    @Column(length = 50)
    private String primaryCropType;
    @Column(length = 50)
    private String farmingMethodology;
    @Column
    private Integer farmingExperienceYears;
    @Column(length = 100)
    private String bankAccountNumber;
    @Column(length = 50)
    private String ifscCode;
    @Column
    private Boolean isSchemeEnrolled;
    @Column(length = 100)
    private String talukName;
    @Column(length = 100)
    private String districtName;
    @Column(length = 100)
    private String stateName;
    @Column
    private LocalDate lastSoilTestDate;

    public Farmer() {
        super();
        this.isSchemeEnrolled = false;
        this.totalLandHolding = 0.0;
    }

    public Farmer(String username, String passwordHash, String email, String phoneNumber,
                  String fullName, String address, String aadharNumber,
                  String farmerRegistrationId, Double totalLandHolding,
                  String primaryCropType, String districtName, String stateName) {
        super(username, passwordHash, email, phoneNumber, fullName, address, aadharNumber, "FARMER");
        this.farmerRegistrationId = farmerRegistrationId;
        this.totalLandHolding = totalLandHolding;
        this.primaryCropType = primaryCropType;
        this.districtName = districtName;
        this.stateName = stateName;
    }

    @Override
    public boolean authenticate(String rawPassword) {
        if (rawPassword == null || rawPassword.isEmpty()) return false;
        return this.getPasswordHash().equals(String.valueOf(rawPassword.hashCode()));
    }

    @Override
    public String getDashboardSummary() {
        return "Farmer: " + getFullName() + " | Land: " + totalLandHolding + " acres | District: " + districtName;
    }

    @Override
    public String generateReport() {
        StringBuffer report = new StringBuffer();
        report.append("=== FARMER REPORT ===\n");
        report.append("Name        : ").append(getFullName()).append("\n");
        report.append("Reg. ID     : ").append(farmerRegistrationId).append("\n");
        report.append("Land (acres): ").append(totalLandHolding).append("\n");
        report.append("Crop Type   : ").append(primaryCropType).append("\n");
        report.append("District    : ").append(districtName).append(", ").append(stateName).append("\n");
        report.append("====================\n");
        return report.toString();
    }

    @Override
    public String getSummary() {
        return farmerRegistrationId + " | " + getFullName() + " | " + districtName;
    }

    @Override
    public boolean isReportDue() {
        if (lastSoilTestDate == null) return true;
        return lastSoilTestDate.plusMonths(6).isBefore(LocalDate.now());
    }

    public boolean isEligibleForLoan(double minimumAcresRequired) {
        return totalLandHolding != null && totalLandHolding >= minimumAcresRequired;
    }

    // Getters and Setters
    public String getFarmerRegistrationId() { return farmerRegistrationId; }
    public void setFarmerRegistrationId(String farmerRegistrationId) { this.farmerRegistrationId = farmerRegistrationId; }
    public Double getTotalLandHolding() { return totalLandHolding; }
    public void setTotalLandHolding(Double totalLandHolding) { this.totalLandHolding = totalLandHolding; }
    public String getPrimaryCropType() { return primaryCropType; }
    public void setPrimaryCropType(String primaryCropType) { this.primaryCropType = primaryCropType; }
    public String getFarmingMethodology() { return farmingMethodology; }
    public void setFarmingMethodology(String farmingMethodology) { this.farmingMethodology = farmingMethodology; }
    public Integer getFarmingExperienceYears() { return farmingExperienceYears; }
    public void setFarmingExperienceYears(Integer farmingExperienceYears) { this.farmingExperienceYears = farmingExperienceYears; }
    public String getBankAccountNumber() { return bankAccountNumber; }
    public void setBankAccountNumber(String bankAccountNumber) { this.bankAccountNumber = bankAccountNumber; }
    public String getIfscCode() { return ifscCode; }
    public void setIfscCode(String ifscCode) { this.ifscCode = ifscCode; }
    public Boolean getIsSchemeEnrolled() { return isSchemeEnrolled; }
    public void setIsSchemeEnrolled(Boolean isSchemeEnrolled) { this.isSchemeEnrolled = isSchemeEnrolled; }
    public String getTalukName() { return talukName; }
    public void setTalukName(String talukName) { this.talukName = talukName; }
    public String getDistrictName() { return districtName; }
    public void setDistrictName(String districtName) { this.districtName = districtName; }
    public String getStateName() { return stateName; }
    public void setStateName(String stateName) { this.stateName = stateName; }
    public LocalDate getLastSoilTestDate() { return lastSoilTestDate; }
    public void setLastSoilTestDate(LocalDate lastSoilTestDate) { this.lastSoilTestDate = lastSoilTestDate; }
}
