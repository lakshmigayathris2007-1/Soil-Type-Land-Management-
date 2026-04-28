package com.soilmanagement.service;

import com.soilmanagement.domain.Land;
import com.soilmanagement.exception.LandNotFoundException;
import com.soilmanagement.repository.LandRepository;
import com.soilmanagement.thread.ReportGenerationThread;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * LandService
 * Business logic for Land parcel management.
 */
@Service
@Transactional
public class LandService {

    private final LandRepository        landRepository;
    private final ReportGenerationThread reportThread;

    public LandService(LandRepository landRepository,
                       ReportGenerationThread reportThread) {
        this.landRepository = landRepository;
        this.reportThread   = reportThread;
    }

    public Land createLand(Land land) {
        if (land == null) throw new NullPointerException("Land cannot be null");
        if (land.getAreaInAcres() == null || land.getAreaInAcres() <= 0)
            throw new IllegalArgumentException("Land area must be positive");
        if (landRepository.existsBySurveyNumber(land.getSurveyNumber()))
            throw new IllegalArgumentException("Survey number already registered: " + land.getSurveyNumber());

        Land saved = landRepository.save(land);
        reportThread.addLandForReport(saved);
        return saved;
    }

    @Transactional(readOnly = true)
    public Land getById(Long id) {
        return landRepository.findById(id)
            .orElseThrow(() -> new LandNotFoundException(id));
    }

    @Transactional(readOnly = true)
    public Land getBySurveyNumber(String surveyNumber) {
        return landRepository.findBySurveyNumber(surveyNumber)
            .orElseThrow(() -> new LandNotFoundException(surveyNumber));
    }

    @Transactional(readOnly = true)
    public List<Land> findAll() {
        return landRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Land> findByDistrict(String district) {
        return landRepository.findByDistrict(district);
    }

    public Land updateLand(Long id, Land updated) {
        Land existing = landRepository.findById(id)
            .orElseThrow(() -> new LandNotFoundException(id));

        existing.setAreaInAcres(updated.getAreaInAcres());
        existing.setLandClassification(updated.getLandClassification());
        existing.setIrrigationSourceType(updated.getIrrigationSourceType());
        existing.setElevationMeters(updated.getElevationMeters());
        return landRepository.save(existing);
    }

    public void deleteLand(Long id) {
        if (!landRepository.existsById(id))
            throw new LandNotFoundException(id);
        landRepository.deleteById(id);
    }

    /**
     * Farmer → lands mapping using HashMap.
     * Demonstrates HashMap<farmerId, List<Land>> — Java Collections concept.
     */
    @Transactional(readOnly = true)
    public Map<Long, List<Land>> getLandsByFarmer() {
        List<Land> all = landRepository.findAll();
        Map<Long, List<Land>> map = new HashMap<>();
        for (Land l : all) {
            if (l.getOwner() != null) {
                map.computeIfAbsent(l.getOwner().getId(), k -> new ArrayList<>()).add(l);
            }
        }
        return map;
    }

    /** Sort lands by area ascending — Comparator usage */
    @Transactional(readOnly = true)
    public List<Land> getLandsSortedByArea() {
        List<Land> lands = new ArrayList<>(landRepository.findAll());
        lands.sort(Comparator.comparingDouble(Land::getAreaInAcres));
        return lands;
    }
}
