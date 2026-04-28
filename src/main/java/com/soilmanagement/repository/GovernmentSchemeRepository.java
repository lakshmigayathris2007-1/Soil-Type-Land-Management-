package com.soilmanagement.repository;

import com.soilmanagement.domain.GovernmentScheme;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface GovernmentSchemeRepository extends JpaRepository<GovernmentScheme, Long> {
    Optional<GovernmentScheme> findBySchemeCode(String schemeCode);
    List<GovernmentScheme> findBySchemeType(String type);

    @Query("SELECT gs FROM GovernmentScheme gs WHERE gs.applicationStartDate <= :today AND gs.applicationEndDate >= :today")
    List<GovernmentScheme> findOpenSchemes(@Param("today") LocalDate today);
}
