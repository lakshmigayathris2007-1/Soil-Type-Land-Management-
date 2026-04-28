package com.soilmanagement.domain;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "government_schemes")
public class GovernmentScheme {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 30)
    private String schemeCode;

    @Column(nullable = false, length = 200)
    private String schemeName;

    @Column(nullable = false, length = 50)
    private String schemeType;

    @Column(nullable = false)
    private Double benefitAmountRupees;

    @Column(nullable = false)
    private LocalDate applicationStartDate;

    @Column(nullable = false)
    private LocalDate applicationEndDate;

    @Column(length = 500)
    private String eligibilityCriteria;

    @Column
    private Double minLandHoldingAcres;

    @Column
    private Double maxAnnualIncomeRupees;

    @Column(length = 100)
    private String nodeDepartment;

    @Column
    private Integer maxBeneficiaries;

    @Column
    private Integer currentEnrollmentCount;

    @Column(length = 200)
    private String documentationRequired;

    @Transient
    private Set<String> enrolledFarmerIds = new HashSet<>();

    public GovernmentScheme() {}

    public boolean isEnrolmentOpen() {
        LocalDate today = LocalDate.now();
        boolean dateOk  = !today.isBefore(applicationStartDate) && !today.isAfter(applicationEndDate);
        boolean slotsOk = maxBeneficiaries == null
                || (currentEnrollmentCount != null && currentEnrollmentCount < maxBeneficiaries);
        return dateOk && slotsOk;
    }

    public boolean enrollFarmer(String farmerRegistrationId) {
        if (!isEnrolmentOpen()) return false;
        boolean added = enrolledFarmerIds.add(farmerRegistrationId);
        if (added && currentEnrollmentCount != null) currentEnrollmentCount++;
        return added;
    }

    public boolean isFarmerEnrolled(String farmerRegistrationId) {
        return enrolledFarmerIds.contains(farmerRegistrationId);
    }

    public boolean isFarmerEligible(double landAcres, double annualIncomeRupees) {
        boolean landOk   = minLandHoldingAcres  == null || landAcres         >= minLandHoldingAcres;
        boolean incomeOk = maxAnnualIncomeRupees == null || annualIncomeRupees <= maxAnnualIncomeRupees;
        return landOk && incomeOk;
    }

    public Set<String> getRequiredDocumentsSet() {
        Set<String> docs = new HashSet<>();
        if (documentationRequired != null)
            for (String doc : documentationRequired.split(","))
                docs.add(doc.trim().toUpperCase());
        return docs;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getSchemeCode() { return schemeCode; }
    public void setSchemeCode(String v) { this.schemeCode = v; }
    public String getSchemeName() { return schemeName; }
    public void setSchemeName(String v) { this.schemeName = v; }
    public String getSchemeType() { return schemeType; }
    public void setSchemeType(String v) { this.schemeType = v; }
    public Double getBenefitAmountRupees() { return benefitAmountRupees; }
    public void setBenefitAmountRupees(Double v) { this.benefitAmountRupees = v; }
    public LocalDate getApplicationStartDate() { return applicationStartDate; }
    public void setApplicationStartDate(LocalDate v) { this.applicationStartDate = v; }
    public LocalDate getApplicationEndDate() { return applicationEndDate; }
    public void setApplicationEndDate(LocalDate v) { this.applicationEndDate = v; }
    public String getEligibilityCriteria() { return eligibilityCriteria; }
    public void setEligibilityCriteria(String v) { this.eligibilityCriteria = v; }
    public Double getMinLandHoldingAcres() { return minLandHoldingAcres; }
    public void setMinLandHoldingAcres(Double v) { this.minLandHoldingAcres = v; }
    public Double getMaxAnnualIncomeRupees() { return maxAnnualIncomeRupees; }
    public void setMaxAnnualIncomeRupees(Double v) { this.maxAnnualIncomeRupees = v; }
    public String getNodeDepartment() { return nodeDepartment; }
    public void setNodeDepartment(String v) { this.nodeDepartment = v; }
    public Integer getMaxBeneficiaries() { return maxBeneficiaries; }
    public void setMaxBeneficiaries(Integer v) { this.maxBeneficiaries = v; }
    public Integer getCurrentEnrollmentCount() { return currentEnrollmentCount; }
    public void setCurrentEnrollmentCount(Integer v) { this.currentEnrollmentCount = v; }
    public String getDocumentationRequired() { return documentationRequired; }
    public void setDocumentationRequired(String v) { this.documentationRequired = v; }
}
