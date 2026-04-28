package com.soilmanagement.controller;

import com.soilmanagement.domain.Farmer;
import com.soilmanagement.service.FarmerService;
import com.soilmanagement.service.JdbcDemoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/farmer")
@CrossOrigin(origins = "*")
public class FarmerController {

    private final FarmerService   farmerService;
    private final JdbcDemoService jdbcService;

    public FarmerController(FarmerService farmerService, JdbcDemoService jdbcService) {
        this.farmerService = farmerService;
        this.jdbcService   = jdbcService;
    }

    /** POST /api/farmer — Register farmer */
    @PostMapping
    public ResponseEntity<Farmer> registerFarmer(@RequestBody Farmer farmer) {
        return ResponseEntity.status(HttpStatus.CREATED).body(farmerService.registerFarmer(farmer));
    }

    /** GET /api/farmer/{id} */
    @GetMapping("/{id}")
    public ResponseEntity<Farmer> getById(@PathVariable Long id) {
        return ResponseEntity.ok(farmerService.findById(id));
    }

    /** GET /api/farmer — List all farmers */
    @GetMapping
    public ResponseEntity<List<Farmer>> getAll() {
        return ResponseEntity.ok(farmerService.findAll());
    }

    /** GET /api/farmer/district/{district} */
    @GetMapping("/district/{district}")
    public ResponseEntity<List<Farmer>> getByDistrict(@PathVariable String district) {
        return ResponseEntity.ok(farmerService.findByDistrict(district));
    }

    /** PUT /api/farmer/{id} */
    @PutMapping("/{id}")
    public ResponseEntity<Farmer> updateFarmer(@PathVariable Long id, @RequestBody Farmer farmer) {
        return ResponseEntity.ok(farmerService.updateFarmer(id, farmer));
    }

    /** DELETE /api/farmer/{id} */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFarmer(@PathVariable Long id) {
        farmerService.deleteFarmer(id);
        return ResponseEntity.noContent().build();
    }

    /** GET /api/farmer/summaries — Dashboard summaries (ArrayList + Iterator demo) */
    @GetMapping("/summaries")
    public ResponseEntity<List<String>> getSummaries() {
        return ResponseEntity.ok(farmerService.getAllFarmerSummaries());
    }

    /** GET /api/farmer/count/{state} — JDBC: count farmers in a state */
    @GetMapping("/count/{state}")
    public ResponseEntity<Map<String, Object>> countByState(@PathVariable String state) {
        int count = jdbcService.countFarmersByState(state);
        return ResponseEntity.ok(Map.of("state", state, "farmerCount", count));
    }
}
