package com.soilmanagement.controller;

import com.soilmanagement.domain.Soil;
import com.soilmanagement.service.SoilService;
import com.soilmanagement.service.JdbcDemoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * SoilController
 * REST controller exposing CRUD endpoints for Soil management.
 *
 * Sample request/response JSON shown in method Javadoc.
 */
@RestController
@RequestMapping("/api/soil")
@CrossOrigin(origins = "*")
public class SoilController {

    private final SoilService     soilService;
    private final JdbcDemoService jdbcService;

    public SoilController(SoilService soilService, JdbcDemoService jdbcService) {
        this.soilService  = soilService;
        this.jdbcService  = jdbcService;
    }

    /**
     * POST /api/soil
     * Create a new soil record.
     *
     * Request JSON:
     * {
     *   "soilSampleCode": "SS-KA-2024-010",
     *   "soilType": "Alluvial",
     *   "phLevel": 6.8,
     *   "organicCarbonPercent": 1.85,
     *   "nitrogenContentKgPerHa": 180.5,
     *   "phosphorusContentKgPerHa": 22.3,
     *   "potassiumContentKgPerHa": 145.0,
     *   "texture": "Loamy",
     *   "sampleCollectionDate": "2024-01-15",
     *   "collectionLocation": "Mysuru District, Karnataka"
     * }
     *
     * Response: 201 Created with created Soil JSON
     */
    @PostMapping
    public ResponseEntity<Soil> createSoil(@RequestBody Soil soil) {
        Soil created = soilService.createSoil(soil);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * GET /api/soil/{id}
     * Retrieve soil by ID.
     * Response: 200 OK with Soil JSON, or 404 if not found.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Soil> getSoilById(@PathVariable Long id) {
        return soilService.findById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    /**
     * GET /api/soil
     * Get all soil records, optionally sorted by fertility.
     * Query param: ?sortByFertility=true
     */
    @GetMapping
    public ResponseEntity<List<Soil>> getAllSoils(
            @RequestParam(required = false, defaultValue = "false") boolean sortByFertility) {
        List<Soil> soils = sortByFertility
            ? soilService.getSoilsSortedByFertility()
            : soilService.findAll();
        return ResponseEntity.ok(soils);
    }

    /**
     * GET /api/soil/type/{soilType}
     * Retrieve soils by type (e.g., Alluvial, Black, Red).
     */
    @GetMapping("/type/{soilType}")
    public ResponseEntity<List<Soil>> getSoilsByType(@PathVariable String soilType) {
        return ResponseEntity.ok(soilService.findBySoilType(soilType));
    }

    /**
     * PUT /api/soil/{id}
     * Update an existing soil record's measurement values.
     *
     * Request JSON: same structure as POST
     * Response: 200 OK with updated Soil JSON
     */
    @PutMapping("/{id}")
    public ResponseEntity<Soil> updateSoil(@PathVariable Long id, @RequestBody Soil soil) {
        return ResponseEntity.ok(soilService.updateSoil(id, soil));
    }

    /**
     * DELETE /api/soil/{id}
     * Delete a soil record by ID.
     * Response: 204 No Content
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSoil(@PathVariable Long id) {
        soilService.deleteSoil(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * GET /api/soil/{id}/nutrients
     * Get nutrient deficiency report for a soil sample.
     *
     * Response JSON:
     * {
     *   "NITROGEN_DEFICIENT": false,
     *   "PHOSPHORUS_DEFICIENT": true,
     *   "POTASSIUM_DEFICIENT": false,
     *   "AMENDMENT_NEEDED": true
     * }
     */
    @GetMapping("/{id}/nutrients")
    public ResponseEntity<Map<String, Boolean>> getNutrientReport(@PathVariable Long id) {
        return ResponseEntity.ok(soilService.getNutrientDeficiencyReport(id));
    }

    /**
     * GET /api/soil/grouped
     * Get soils grouped by type — returns Map<String, List<Soil>>.
     */
    @GetMapping("/grouped")
    public ResponseEntity<Map<String, List<Soil>>> getSoilsGrouped() {
        return ResponseEntity.ok(soilService.groupSoilsByType());
    }

    // ── JDBC demo endpoints ──────────────────────────────────

    /**
     * GET /api/soil/stats/by-type
     * Raw JDBC: count soils grouped by type.
     */
    @GetMapping("/stats/by-type")
    public ResponseEntity<List<Map<String, Object>>> getStatsByType() {
        return ResponseEntity.ok(jdbcService.countSoilsByType());
    }

    /**
     * GET /api/soil/stats/ph
     * Raw JDBC: pH statistics (min, max, avg).
     */
    @GetMapping("/stats/ph")
    public ResponseEntity<Map<String, Object>> getPhStats() {
        return ResponseEntity.ok(jdbcService.getSoilPhStats());
    }
}
