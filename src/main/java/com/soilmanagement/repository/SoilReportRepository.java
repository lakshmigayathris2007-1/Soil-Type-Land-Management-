package com.soilmanagement.repository;

import com.soilmanagement.domain.SoilReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SoilReportRepository extends JpaRepository<SoilReport, Long> {
    Optional<SoilReport> findByReportNumber(String reportNumber);
    List<SoilReport> findByRequestedByFarmer_Id(Long farmerId);
    List<SoilReport> findByOverallRating(String rating);

    @Query("SELECT sr FROM SoilReport sr WHERE sr.requiresImmediateAction = true")
    List<SoilReport> findUrgentReports();
}
