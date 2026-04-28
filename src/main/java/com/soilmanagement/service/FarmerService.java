package com.soilmanagement.service;

import com.soilmanagement.domain.Farmer;
import com.soilmanagement.repository.FarmerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * FarmerService — business logic for Farmer management.
 */
@Service
@Transactional
public class FarmerService {

    private final FarmerRepository farmerRepository;

    public FarmerService(FarmerRepository farmerRepository) {
        this.farmerRepository = farmerRepository;
    }

    public Farmer registerFarmer(Farmer farmer) {
        if (farmer.getUsername() == null || farmer.getUsername().isBlank())
            throw new IllegalArgumentException("Username is required");
        if (farmerRepository.findByUsername(farmer.getUsername()).isPresent())
            throw new IllegalArgumentException("Username already taken: " + farmer.getUsername());
        // Hash password before persisting (simulated)
        farmer.setPasswordHash(String.valueOf(farmer.getPasswordHash().hashCode()));
        return farmerRepository.save(farmer);
    }

    @Transactional(readOnly = true)
    public Farmer findById(Long id) {
        return farmerRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Farmer not found: " + id));
    }

    @Transactional(readOnly = true)
    public List<Farmer> findAll() {
        return farmerRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Farmer> findByDistrict(String district) {
        return farmerRepository.findByDistrictName(district);
    }

    public Farmer updateFarmer(Long id, Farmer updated) {
        Farmer existing = farmerRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Farmer not found: " + id));
        existing.setFullName(updated.getFullName());
        existing.setPhoneNumber(updated.getPhoneNumber());
        existing.setEmail(updated.getEmail());
        existing.setAddress(updated.getAddress());
        existing.setFarmingMethodology(updated.getFarmingMethodology());
        existing.setTotalLandHolding(updated.getTotalLandHolding());
        return farmerRepository.save(existing);
    }

    public void deleteFarmer(Long id) {
        if (!farmerRepository.existsById(id))
            throw new IllegalArgumentException("Farmer not found: " + id);
        farmerRepository.deleteById(id);
    }

    /** Generate per-farmer dashboard summary strings — ArrayList */
    @Transactional(readOnly = true)
    public List<String> getAllFarmerSummaries() {
        List<Farmer> farmers = farmerRepository.findAll();
        // ArrayList preserves insertion order
        List<String> summaries = new ArrayList<>();
        // Iterator pattern
        Iterator<Farmer> it = farmers.iterator();
        while (it.hasNext()) {
            summaries.add(it.next().getDashboardSummary());
        }
        return summaries;
    }
}
