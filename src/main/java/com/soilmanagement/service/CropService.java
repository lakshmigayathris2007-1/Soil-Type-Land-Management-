package com.soilmanagement.service;

import com.soilmanagement.domain.Crop;
import com.soilmanagement.repository.CropRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * CropService — business logic for Crop management.
 */
@Service
@Transactional
public class CropService {

    private final CropRepository cropRepository;

    public CropService(CropRepository cropRepository) {
        this.cropRepository = cropRepository;
    }

    public Crop createCrop(Crop crop) {
        if (crop.getCropName() == null || crop.getCropName().isBlank())
            throw new IllegalArgumentException("Crop name is required");
        return cropRepository.save(crop);
    }

    @Transactional(readOnly = true)
    public Optional<Crop> findById(Long id) {
        return cropRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<Crop> findAll() {
        return cropRepository.findAll();
    }

    public Crop updateCrop(Long id, Crop updated) {
        Crop existing = cropRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Crop not found: " + id));
        existing.setCropName(updated.getCropName());
        existing.setCropCategory(updated.getCropCategory());
        existing.setGrowthDurationDays(updated.getGrowthDurationDays());
        existing.setExpectedYieldTonsPerAcre(updated.getExpectedYieldTonsPerAcre());
        existing.setWaterRequirementMmPerSeason(updated.getWaterRequirementMmPerSeason());
        return cropRepository.save(existing);
    }

    public void deleteCrop(Long id) {
        if (!cropRepository.existsById(id))
            throw new IllegalArgumentException("Crop not found: " + id);
        cropRepository.deleteById(id);
    }

    /**
     * Group crops by category — HashSet to track unique categories.
     * Demonstrates HashSet usage for de-duplication.
     */
    @Transactional(readOnly = true)
    public Set<String> getAllCropCategories() {
        List<Crop> all = cropRepository.findAll();
        // HashSet ensures unique category names
        Set<String> categories = new HashSet<>();
        for (Crop c : all) {
            if (c.getCropCategory() != null) categories.add(c.getCropCategory());
        }
        return categories;
    }

    /** Sort crops by growth duration — Comparator */
    @Transactional(readOnly = true)
    public List<Crop> getCropsSortedByDuration() {
        List<Crop> crops = new ArrayList<>(cropRepository.findAll());
        crops.sort(Comparator.comparingInt(Crop::getGrowthDurationDays));
        return crops;
    }
}
