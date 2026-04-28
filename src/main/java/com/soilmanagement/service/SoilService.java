package com.soilmanagement.service;

import com.soilmanagement.domain.Soil;
import com.soilmanagement.exception.InvalidSoilDataException;
import com.soilmanagement.repository.SoilRepository;
import com.soilmanagement.thread.ReportGenerationThread;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * SoilService
 * Business logic layer for Soil management.
 * Demonstrates:
 * - Service layer pattern with @Transactional
 * - Validation with custom exception
 * - Collections usage: HashMap, ArrayList, Comparator, Iterator
 */
@Service
@Transactional
public class SoilService {

    private final SoilRepository soilRepository;
    private final ReportGenerationThread reportThread;

    public SoilService(SoilRepository soilRepository,
                       ReportGenerationThread reportThread) {
        this.soilRepository = soilRepository;
        this.reportThread   = reportThread;
    }

    /** Create a new soil record after validation */
    public Soil createSoil(Soil soil) {
        validateSoil(soil);
        if (soilRepository.existsBySoilSampleCode(soil.getSoilSampleCode())) {
            throw new InvalidSoilDataException("soilSampleCode", soil.getSoilSampleCode(),
                "Soil sample code already exists: " + soil.getSoilSampleCode());
        }
        Soil saved = soilRepository.save(soil);
        reportThread.addSoilForReport(saved); // queue for async report
        return saved;
    }

    @Transactional(readOnly = true)
    public Optional<Soil> findById(Long id) {
        return soilRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Optional<Soil> findBySampleCode(String code) {
        return soilRepository.findBySoilSampleCode(code);
    }

    @Transactional(readOnly = true)
    public List<Soil> findAll() {
        return soilRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Soil> findBySoilType(String type) {
        return soilRepository.findBySoilType(type);
    }

    /** Update soil data; only mutable measurement fields are updated */
    public Soil updateSoil(Long id, Soil updatedData) {
        Soil existing = soilRepository.findById(id)
            .orElseThrow(() -> new InvalidSoilDataException("id", id, "Soil not found with id: " + id));
        validateSoil(updatedData);

        existing.setPhLevel(updatedData.getPhLevel());
        existing.setOrganicCarbonPercent(updatedData.getOrganicCarbonPercent());
        existing.setNitrogenContentKgPerHa(updatedData.getNitrogenContentKgPerHa());
        existing.setPhosphorusContentKgPerHa(updatedData.getPhosphorusContentKgPerHa());
        existing.setPotassiumContentKgPerHa(updatedData.getPotassiumContentKgPerHa());
        existing.setTexture(updatedData.getTexture());
        existing.setElectricalConductivityDsPerM(updatedData.getElectricalConductivityDsPerM());

        return soilRepository.save(existing);
    }

    public void deleteSoil(Long id) {
        if (!soilRepository.existsById(id))
            throw new InvalidSoilDataException("id", id, "Soil not found with id: " + id);
        soilRepository.deleteById(id);
    }

    /**
     * Build a HashMap of soilType → List<Soil> for grouped analysis.
     * HashMap usage — Java Collections concept.
     */
    @Transactional(readOnly = true)
    public Map<String, List<Soil>> groupSoilsByType() {
        List<Soil> allSoils = soilRepository.findAll();
        Map<String, List<Soil>> grouped = new HashMap<>();

        // Iterator pattern — explicit traversal
        Iterator<Soil> it = allSoils.iterator();
        while (it.hasNext()) {
            Soil s = it.next();
            // computeIfAbsent initialises the list if key is absent — clean Map usage
            grouped.computeIfAbsent(s.getSoilType(), k -> new ArrayList<>()).add(s);
        }
        return grouped;
    }

    /**
     * Return all soils sorted by fertility index descending.
     * Comparator — Java Collections concept.
     */
    @Transactional(readOnly = true)
    public List<Soil> getSoilsSortedByFertility() {
        List<Soil> soils = new ArrayList<>(soilRepository.findAll());
        soils.sort(Comparator.comparingDouble(Soil::calculateIndex).reversed());
        return soils;
    }

    /** Validate soil data; throw custom exception on failure */
    private void validateSoil(Soil soil) {
        if (soil == null) throw new NullPointerException("Soil object cannot be null");

        if (soil.getPhLevel() == null || soil.getPhLevel() < 0 || soil.getPhLevel() > 14)
            throw new InvalidSoilDataException("phLevel", soil.getPhLevel(),
                "pH must be between 0 and 14");

        if (soil.getOrganicCarbonPercent() == null || soil.getOrganicCarbonPercent() < 0)
            throw new InvalidSoilDataException("organicCarbonPercent",
                soil.getOrganicCarbonPercent(), "Organic carbon cannot be negative");

        if (soil.getSoilSampleCode() == null || soil.getSoilSampleCode().isBlank())
            throw new IllegalArgumentException("Soil sample code is required");

        // String comparison using equals — immutable String concept
        if (soil.getSoilType() == null || soil.getSoilType().equals(""))
            throw new IllegalArgumentException("Soil type is required");
    }

    /** Return deficiency summary for a soil sample */
    @Transactional(readOnly = true)
    public Map<String, Boolean> getNutrientDeficiencyReport(Long soilId) {
        Soil soil = soilRepository.findById(soilId)
            .orElseThrow(() -> new InvalidSoilDataException("id", soilId, "Soil not found: " + soilId));

        Map<String, Boolean> report = new LinkedHashMap<>();
        report.put("NITROGEN_DEFICIENT",   soil.isDeficient("NITROGEN"));
        report.put("PHOSPHORUS_DEFICIENT", soil.isDeficient("PHOSPHORUS"));
        report.put("POTASSIUM_DEFICIENT",  soil.isDeficient("POTASSIUM"));
        report.put("AMENDMENT_NEEDED",     !soil.recommendAmendment()
                                               .startsWith("Soil is within"));
        return report;
    }
}
