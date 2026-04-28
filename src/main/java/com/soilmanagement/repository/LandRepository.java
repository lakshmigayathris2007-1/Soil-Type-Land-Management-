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
// LandRepository
// ─────────────────────────────────────────────────────────
@Repository
public interface LandRepository extends JpaRepository<Land, Long> {

    Optional<Land> findBySurveyNumber(String surveyNumber);

    List<Land> findByDistrict(String district);

    List<Land> findByOwner_Id(Long farmerId);

    List<Land> findByAreaInAcresGreaterThanEqual(Double minArea);

    @Query("SELECT l FROM Land l WHERE l.state = :state AND l.areaInAcres >= :minArea")
    List<Land> findLargeInState(@Param("state") String state, @Param("minArea") Double minArea);

    boolean existsBySurveyNumber(String surveyNumber);
}
