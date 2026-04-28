package com.soilmanagement.controller;

import com.soilmanagement.domain.Land;
import com.soilmanagement.service.LandService;
import com.soilmanagement.service.JdbcDemoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * LandController
 * REST endpoints for Land parcel management.
 */
@RestController
@RequestMapping("/api/land")
@CrossOrigin(origins = "*")
public class LandController {

    private final LandService     landService;
    private final JdbcDemoService jdbcService;

    public LandController(LandService landService, JdbcDemoService jdbcService) {
        this.landService  = landService;
        this.jdbcService  = jdbcService;
    }

    /**
     * POST /api/land
     * Create a new land parcel record.
     *
     * Request JSON:
     * {
     *   "surveyNumber": "SY-KA-999-1A",
     *   "areaInAcres": 4.5,
     *   "village": "Hullahalli",
     *   "taluk": "Nanjangud",
     *   "district": "Mysuru",
     *   "state": "Karnataka",
     *   "landClassification": "Agricultural",
     *   "irrigationSourceType": "Canal",
     *   "elevationMeters": 780.0
     * }
     *
     * Response: 201 Created
     */
    @PostMapping
    public ResponseEntity<Land> createLand(@RequestBody Land land) {
        return ResponseEntity.status(HttpStatus.CREATED)
                             .body(landService.createLand(land));
    }

    /**
     * GET /api/land/{id}
     * Get land details by database ID.
     * Response: 200 OK with Land JSON, or 404 Not Found.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Land> getLandById(@PathVariable Long id) {
        return ResponseEntity.ok(landService.getById(id));
    }

    /**
     * GET /api/land/survey/{surveyNumber}
     * Get land details by government survey number.
     */
    @GetMapping("/survey/{surveyNumber}")
    public ResponseEntity<Land> getLandBySurvey(@PathVariable String surveyNumber) {
        return ResponseEntity.ok(landService.getBySurveyNumber(surveyNumber));
    }

    /**
     * GET /api/land
     * List all land parcels.
     * Optional query: ?sortByArea=true
     */
    @GetMapping
    public ResponseEntity<List<Land>> getAllLands(
            @RequestParam(required = false, defaultValue = "false") boolean sortByArea) {
        List<Land> lands = sortByArea
            ? landService.getLandsSortedByArea()
            : landService.findAll();
        return ResponseEntity.ok(lands);
    }

    /**
     * GET /api/land/district/{district}
     * Get all land parcels in a district.
     */
    @GetMapping("/district/{district}")
    public ResponseEntity<List<Land>> getLandsByDistrict(@PathVariable String district) {
        return ResponseEntity.ok(landService.findByDistrict(district));
    }

    /**
     * PUT /api/land/{id}
     * Update land parcel classification / irrigation details.
     *
     * Request JSON:
     * {
     *   "areaInAcres": 5.0,
     *   "landClassification": "Horticultural",
     *   "irrigationSourceType": "Drip",
     *   "elevationMeters": 800.0
     * }
     *
     * Response: 200 OK with updated Land JSON
     */
    @PutMapping("/{id}")
    public ResponseEntity<Land> updateLand(@PathVariable Long id, @RequestBody Land land) {
        return ResponseEntity.ok(landService.updateLand(id, land));
    }

    /**
     * DELETE /api/land/{id}
     * Remove a land parcel record.
     * Response: 204 No Content
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLand(@PathVariable Long id) {
        landService.deleteLand(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * GET /api/land/by-farmer
     * Returns HashMap<farmerId, List<Land>>.
     */
    @GetMapping("/by-farmer")
    public ResponseEntity<Map<Long, List<Land>>> getLandsByFarmer() {
        return ResponseEntity.ok(landService.getLandsByFarmer());
    }

    /**
     * GET /api/land/stats/by-district
     * Raw JDBC: land area statistics grouped by district.
     *
     * Response JSON:
     * [
     *   {"district": "Mysuru", "num_parcels": 5, "total_acres": 24.5, "avg_acres": 4.9},
     *   ...
     * ]
     */
    @GetMapping("/stats/by-district")
    public ResponseEntity<List<Map<String, Object>>> getLandStatsByDistrict() {
        return ResponseEntity.ok(jdbcService.getLandAreaByDistrict());
    }
}
