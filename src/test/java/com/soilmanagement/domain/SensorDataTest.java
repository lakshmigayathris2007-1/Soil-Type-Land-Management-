package com.soilmanagement.domain;

import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * SensorDataTest — tests StringTokenizer-based CSV payload parsing.
 */
class SensorDataTest {

    @Test
    void testParseRawPayload() {
        SensorData sensor = new SensorData();
        sensor.setSensorId("SENS-001");
        sensor.setSensorType("SOIL_MOISTURE");
        sensor.setCapturedAt(LocalDateTime.now());
        sensor.setPrimaryReadingValue(38.5);
        sensor.setPrimaryReadingUnit("%");
        sensor.setRawPayload("SENS-001,SOIL_MOISTURE,38.50,%,12.0,V,90.0,%,-68");

        Map<String, String> fields = sensor.parseRawPayload();

        assertEquals("SENS-001",      fields.get("sensorId"));
        assertEquals("SOIL_MOISTURE", fields.get("sensorType"));
        assertEquals("38.50",         fields.get("primaryValue"));
        assertEquals("%",             fields.get("primaryUnit"));
        assertEquals("90.0",          fields.get("battery"));
    }

    @Test
    void testAnomalyDetection() {
        SensorData sensor = new SensorData();
        sensor.setSensorType("SOIL_MOISTURE");
        sensor.setPrimaryReadingValue(150.0); // invalid — over 100
        assertTrue(sensor.isAnomalousReading());
    }

    @Test
    void testNormalReadingNotAnomalous() {
        SensorData sensor = new SensorData();
        sensor.setSensorType("SOIL_MOISTURE");
        sensor.setPrimaryReadingValue(45.0);
        assertFalse(sensor.isAnomalousReading());
    }

    @Test
    void testBatteryStatus() {
        SensorData sensor = new SensorData();
        sensor.setBatteryLevelPercent(85.0);
        assertEquals("GOOD", sensor.getBatteryStatus());

        sensor.setBatteryLevelPercent(20.0);
        assertEquals("CRITICAL", sensor.getBatteryStatus());

        sensor.setBatteryLevelPercent(50.0);
        assertEquals("LOW", sensor.getBatteryStatus());
    }

    @Test
    void testLoadFromPayload() {
        SensorData sensor = new SensorData();
        sensor.setRawPayload("SENS-002,SOIL_TEMP,27.30,C,12.0,V,75.0,%,-72");
        sensor.loadFromPayload();
        assertEquals(27.30, sensor.getPrimaryReadingValue(), 0.01);
        assertEquals(75.0, sensor.getBatteryLevelPercent(), 0.01);
    }
}
