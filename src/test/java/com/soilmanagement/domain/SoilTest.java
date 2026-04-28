package com.soilmanagement.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * SoilTest — unit tests for Soil domain logic.
 * Tests calculateIndex(), analyzeCondition(), isDeficient(), meetsStandard().
 */
class SoilTest {

    private Soil excellentSoil;
    private Soil poorSoil;

    @BeforeEach
    void setUp() {
        excellentSoil = Soil.builder()
            .soilSampleCode("TEST-001")
            .soilType("Alluvial")
            .phLevel(6.8)
            .organicCarbonPercent(2.0)
            .nitrogenContentKgPerHa(200.0)
            .phosphorusContentKgPerHa(25.0)
            .potassiumContentKgPerHa(180.0)
            .waterHoldingCapacityPercent(45.0)
            .electricalConductivityDsPerM(0.4)
            .texture("Loamy")
            .bulkDensityGPerCm3(1.3)
            .sampleCollectionDate(LocalDate.now())
            .collectionLocation("Test Location")
            .build();

        poorSoil = Soil.builder()
            .soilSampleCode("TEST-002")
            .soilType("Desert")
            .phLevel(4.0)
            .organicCarbonPercent(0.2)
            .nitrogenContentKgPerHa(50.0)
            .phosphorusContentKgPerHa(3.0)
            .potassiumContentKgPerHa(40.0)
            .waterHoldingCapacityPercent(10.0)
            .electricalConductivityDsPerM(3.5)
            .texture("Sandy")
            .bulkDensityGPerCm3(1.7)
            .sampleCollectionDate(LocalDate.now())
            .collectionLocation("Desert Region")
            .build();
    }

    @Test
    void testExcellentSoilFertilityIndex() {
        double index = excellentSoil.calculateIndex();
        assertTrue(index >= 60, "Excellent soil should have index >= 60, got: " + index);
    }

    @Test
    void testPoorSoilFertilityIndex() {
        double index = poorSoil.calculateIndex();
        assertTrue(index < 40, "Poor soil should have index < 40, got: " + index);
    }

    @Test
    void testAnalyzeConditionExcellent() {
        String condition = excellentSoil.analyzeCondition();
        assertTrue(condition.equals("EXCELLENT") || condition.equals("GOOD"),
            "Expected EXCELLENT or GOOD, got: " + condition);
    }

    @Test
    void testAnalyzeConditionPoor() {
        assertEquals("POOR", poorSoil.analyzeCondition());
    }

    @Test
    void testNitrogenDeficiency() {
        // poorSoil has N=50, threshold=140
        assertTrue(poorSoil.isDeficient("NITROGEN"));
        assertFalse(excellentSoil.isDeficient("NITROGEN"));
    }

    @Test
    void testPhosphorusDeficiencyCustomThreshold() {
        assertTrue(poorSoil.isDeficient("PHOSPHORUS", 10.0));
        assertFalse(excellentSoil.isDeficient("PHOSPHORUS", 10.0));
    }

    @Test
    void testMeetsOrganicFarmingStandard() {
        assertTrue(excellentSoil.meetsStandard("ORGANIC_FARMING"));
        assertFalse(poorSoil.meetsStandard("ORGANIC_FARMING"));
    }

    @Test
    void testMeetsBasicAgricultureStandard() {
        assertTrue(excellentSoil.meetsStandard("BASIC_AGRICULTURE"));
        assertFalse(poorSoil.meetsStandard("BASIC_AGRICULTURE")); // pH 4.0 is too low
    }

    @Test
    void testRecommendAmendmentForAcidicSoil() {
        String recommendation = poorSoil.recommendAmendment();
        assertTrue(recommendation.toLowerCase().contains("lime") ||
                   recommendation.toLowerCase().contains("ph"),
            "Expected lime recommendation, got: " + recommendation);
    }
}
