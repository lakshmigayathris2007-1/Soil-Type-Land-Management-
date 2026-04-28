package com.soilmanagement.controller;

import com.soilmanagement.domain.Crop;
import com.soilmanagement.service.CropService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/crop")
@CrossOrigin(origins = "*")
public class CropController {

    private final CropService cropService;

    public CropController(CropService cropService) {
        this.cropService = cropService;
    }

    /** POST /api/crop — Create crop */
    @PostMapping
    public ResponseEntity<Crop> createCrop(@RequestBody Crop crop) {
        return ResponseEntity.status(HttpStatus.CREATED).body(cropService.createCrop(crop));
    }

    /** GET /api/crop/{id} — Get by ID */
    @GetMapping("/{id}")
    public ResponseEntity<Crop> getCropById(@PathVariable Long id) {
        return cropService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /** GET /api/crop — List all crops */
    @GetMapping
    public ResponseEntity<List<Crop>> getAllCrops() {
        return ResponseEntity.ok(cropService.findAll());
    }

    /** PUT /api/crop/{id} — Update crop info */
    @PutMapping("/{id}")
    public ResponseEntity<Crop> updateCrop(@PathVariable Long id, @RequestBody Crop crop) {
        return ResponseEntity.ok(cropService.updateCrop(id, crop));
    }

    /** DELETE /api/crop/{id} — Delete crop */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCrop(@PathVariable Long id) {
        cropService.deleteCrop(id);
        return ResponseEntity.noContent().build();
    }

    /** GET /api/crop/categories — All unique crop categories (HashSet demo) */
    @GetMapping("/categories")
    public ResponseEntity<Set<String>> getCategories() {
        return ResponseEntity.ok(cropService.getAllCropCategories());
    }

    /** GET /api/crop/sorted — Crops sorted by growth duration (Comparator demo) */
    @GetMapping("/sorted")
    public ResponseEntity<List<Crop>> getSortedCrops() {
        return ResponseEntity.ok(cropService.getCropsSortedByDuration());
    }
}
