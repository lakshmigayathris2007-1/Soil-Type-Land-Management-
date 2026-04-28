package com.soilmanagement.repository;

import com.soilmanagement.domain.Farmer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FarmerRepository extends JpaRepository<Farmer, Long> {

    Optional<Farmer> findByFarmerRegistrationId(String registrationId);
    Optional<Farmer> findByUsername(String username);
    List<Farmer> findByDistrictName(String districtName);
    List<Farmer> findByStateName(String stateName);
    List<Farmer> findByIsSchemeEnrolled(Boolean enrolled);

    @Query("SELECT f FROM Farmer f WHERE f.totalLandHolding >= :minAcres AND f.stateName = :state")
    List<Farmer> findLargeFarmersInState(@Param("minAcres") Double minAcres,
                                         @Param("state")    String state);
}
