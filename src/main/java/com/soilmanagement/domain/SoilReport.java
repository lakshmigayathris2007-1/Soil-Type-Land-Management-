package com.soilmanagement.domain;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "soil_reports")
public class SoilReport implements Reportable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 30)
    private String reportNumber;

    @Column(nullable = false)
    private LocalDate reportDate;

    @Column(nullable = false, length = 50)
    private String reportingLaboratory;

    @Column(nullable = false, length = 100)
    private String accreditationNumber;

    @Column(length = 1000)
    private String findings;

    @Column(length = 1000)
    private String recommendations;

    @Column(nullable = false, length = 20)
    private String overallRating;

    @Column
    private Double fertilityIndexScore;

    @Column
    private Boolean requiresImmediateAction;

    @Column(length = 200)
    private String followUpActions;

    @Column
    private LocalDate nextTestRecommendedDate;

    @Column(length = 100)
    private String preparedByName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "soil_id")
    private Soil referencedSoil;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "farmer_id")
    private Farmer requestedByFarmer;

    public SoilReport() {}

    @Override
    public String generateReport() {
        StringBuffer sb = new StringBuffer();
        sb.append("====== SOIL ANALYSIS REPORT ======\n");
        sb.append("Report No.  : ").append(reportNumber).append("\n");
        sb.append("Date        : ").append(reportDate).append("\n");
        sb.append("Laboratory  : ").append(reportingLaboratory).append("\n");
        sb.append("Accreditation: ").append(accreditationNumber).append("\n");
        sb.append("Rating      : ").append(overallRating).append("\n");
        sb.append("Score       : ").append(fertilityIndexScore).append("/100\n");
        sb.append("Findings    :\n").append(findings).append("\n");
        sb.append("Recommendations:\n").append(recommendations).append("\n");
        sb.append("Next Test   : ").append(nextTestRecommendedDate).append("\n");
        sb.append("Prepared By : ").append(preparedByName).append("\n");
        sb.append("==================================\n");
        return sb.toString();
    }

    @Override
    public String getSummary() {
        return reportNumber + " | " + overallRating + " | Score: " + fertilityIndexScore;
    }

    @Override
    public boolean isReportDue() {
        if (nextTestRecommendedDate == null) return true;
        return LocalDate.now().isAfter(nextTestRecommendedDate);
    }

    public String getCriticalFlags() {
        StringBuilder flags = new StringBuilder();
        if (Boolean.TRUE.equals(requiresImmediateAction))
            flags.append("[URGENT] ").append(followUpActions);
        if (fertilityIndexScore != null && fertilityIndexScore < 30)
            flags.append(" [LOW FERTILITY — intervention required]");
        return flags.length() > 0 ? flags.toString() : "No critical flags";
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getReportNumber() { return reportNumber; }
    public void setReportNumber(String v) { this.reportNumber = v; }
    public LocalDate getReportDate() { return reportDate; }
    public void setReportDate(LocalDate v) { this.reportDate = v; }
    public String getReportingLaboratory() { return reportingLaboratory; }
    public void setReportingLaboratory(String v) { this.reportingLaboratory = v; }
    public String getAccreditationNumber() { return accreditationNumber; }
    public void setAccreditationNumber(String v) { this.accreditationNumber = v; }
    public String getFindings() { return findings; }
    public void setFindings(String v) { this.findings = v; }
    public String getRecommendations() { return recommendations; }
    public void setRecommendations(String v) { this.recommendations = v; }
    public String getOverallRating() { return overallRating; }
    public void setOverallRating(String v) { this.overallRating = v; }
    public Double getFertilityIndexScore() { return fertilityIndexScore; }
    public void setFertilityIndexScore(Double v) { this.fertilityIndexScore = v; }
    public Boolean getRequiresImmediateAction() { return requiresImmediateAction; }
    public void setRequiresImmediateAction(Boolean v) { this.requiresImmediateAction = v; }
    public String getFollowUpActions() { return followUpActions; }
    public void setFollowUpActions(String v) { this.followUpActions = v; }
    public LocalDate getNextTestRecommendedDate() { return nextTestRecommendedDate; }
    public void setNextTestRecommendedDate(LocalDate v) { this.nextTestRecommendedDate = v; }
    public String getPreparedByName() { return preparedByName; }
    public void setPreparedByName(String v) { this.preparedByName = v; }
    public Soil getReferencedSoil() { return referencedSoil; }
    public void setReferencedSoil(Soil v) { this.referencedSoil = v; }
    public Farmer getRequestedByFarmer() { return requestedByFarmer; }
    public void setRequestedByFarmer(Farmer v) { this.requestedByFarmer = v; }
}
