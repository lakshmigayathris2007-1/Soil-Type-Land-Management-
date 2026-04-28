package com.soilmanagement.repository;

import com.soilmanagement.domain.Crop;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CropRepository extends JpaRepository<Crop, Long> {
    List<Crop> findByCropCategory(String category);
    List<Crop> findByMinPhRequiredLessThanEqualAndMaxPhRequiredGreaterThanEqual(Double ph, Double ph2);
    Optional<Crop> findByCropName(String cropName);
}
