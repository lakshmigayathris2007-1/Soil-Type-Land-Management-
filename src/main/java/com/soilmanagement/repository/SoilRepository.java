package com.soilmanagement.repository;

import com.soilmanagement.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

// ─────────────────────────────────────────────────────────
// SoilRepository
// ─────────────────────────────────────────────────────────
@Repository
public interface SoilRepository extends JpaRepository<Soil, Long> {

    Optional<Soil> findBySoilSampleCode(String soilSampleCode);

    List<Soil> findBySoilType(String soilType);

    List<Soil> findByPhLevelBetween(Double minPh, Double maxPh);

    @Query("SELECT s FROM Soil s WHERE s.organicCarbonPercent >= :minOc ORDER BY s.organicCarbonPercent DESC")
    List<Soil> findHighOrganicCarbonSoils(@Param("minOc") Double minOc);

    @Query("SELECT s FROM Soil s WHERE s.phLevel < 5.5 OR s.phLevel > 8.5")
    List<Soil> findAcidicOrAlkalineSoils();

    boolean existsBySoilSampleCode(String soilSampleCode);
}
