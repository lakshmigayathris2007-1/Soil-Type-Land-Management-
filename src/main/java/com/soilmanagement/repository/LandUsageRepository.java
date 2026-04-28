package com.soilmanagement.repository;

import com.soilmanagement.domain.LandUsage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface LandUsageRepository extends JpaRepository<LandUsage, Long> {
    List<LandUsage> findByLand_Id(Long landId);
    List<LandUsage> findBySeason(String season);
    List<LandUsage> findByUsageType(String usageType);

    @Query("SELECT lu FROM LandUsage lu WHERE lu.usageEndDate IS NULL OR lu.usageEndDate >= :today")
    List<LandUsage> findActiveUsages(@Param("today") LocalDate today);
}
