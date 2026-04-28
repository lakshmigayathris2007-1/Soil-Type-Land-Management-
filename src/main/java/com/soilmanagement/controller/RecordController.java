package com.soilmanagement.controller;

import com.soilmanagement.service.CropService;
import com.soilmanagement.service.FarmerService;
import com.soilmanagement.service.LandService;
import com.soilmanagement.service.SoilService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/record")
@CrossOrigin(origins = "*")
public class RecordController {

    private final SoilService   soilService;
    private final LandService   landService;
    private final CropService   cropService;
    private final FarmerService farmerService;

    public RecordController(SoilService soilService, LandService landService,
                            CropService cropService, FarmerService farmerService) {
        this.soilService   = soilService;
        this.landService   = landService;
        this.cropService   = cropService;
        this.farmerService = farmerService;
    }

    /**
     * DELETE /api/record/{id}?type=SOIL|LAND|CROP|FARMER
     * Generic record deletion endpoint — routes to the correct service.
     * Response: 204 No Content on success, 400 if type is unknown.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRecord(
            @PathVariable Long id,
            @RequestParam String type) {

        // BUG FIX: was incorrectly written as SoilService.deleteSoil(id) (static call)
        // Correct: use the injected instance variable soilService
        switch (type.toUpperCase()) {
            case "SOIL"   -> soilService.deleteSoil(id);
            case "LAND"   -> landService.deleteLand(id);
            case "CROP"   -> cropService.deleteCrop(id);
            case "FARMER" -> farmerService.deleteFarmer(id);
            default       -> throw new IllegalArgumentException("Unknown record type: " + type);
        }
        return ResponseEntity.noContent().build();
    }
}
