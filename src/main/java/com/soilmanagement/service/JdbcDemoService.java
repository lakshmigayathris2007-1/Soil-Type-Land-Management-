package com.soilmanagement.service;

import com.soilmanagement.exception.InvalidSoilDataException;
import com.soilmanagement.exception.LandNotFoundException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * JdbcDemoService
 * Demonstrates raw JDBC query execution via Spring's JdbcTemplate.
 * Used alongside JPA to show both data-access styles.
 *
 * Demonstrates:
 * - Raw SQL with parameter binding
 * - ResultSet processing
 * - try-catch for data-access exceptions
 */
@Service
public class JdbcDemoService {

    private final JdbcTemplate jdbcTemplate;

    public JdbcDemoService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * Raw JDBC: count all soils by type.
     * Returns List<Map> — each Map is one row.
     */
    public List<Map<String, Object>> countSoilsByType() {
        String sql = "SELECT soil_type, COUNT(*) AS total " +
                     "FROM soils GROUP BY soil_type ORDER BY total DESC";
        try {
            return jdbcTemplate.queryForList(sql);
        } catch (Exception e) {
            throw new InvalidSoilDataException("JDBC query failed: " + e.getMessage());
        }
    }

    /**
     * Raw JDBC: fetch soil pH summary statistics.
     */
    public Map<String, Object> getSoilPhStats() {
        String sql = "SELECT MIN(ph_level) AS min_ph, MAX(ph_level) AS max_ph, " +
                     "AVG(ph_level) AS avg_ph FROM soils";
        try {
            return jdbcTemplate.queryForMap(sql);
        } catch (Exception e) {
            throw new InvalidSoilDataException("pH stats query failed: " + e.getMessage());
        }
    }

    /**
     * Raw JDBC: get land area summary by district.
     */
    public List<Map<String, Object>> getLandAreaByDistrict() {
        String sql = "SELECT district, COUNT(*) AS num_parcels, " +
                     "SUM(area_in_acres) AS total_acres, AVG(area_in_acres) AS avg_acres " +
                     "FROM lands GROUP BY district ORDER BY total_acres DESC";
        try {
            return jdbcTemplate.queryForList(sql);
        } catch (Exception e) {
            throw new LandNotFoundException("JDBC land area query failed: " + e.getMessage());
        }
    }

    /**
     * Raw JDBC: count farmers by state using parameterised query.
     */
    public int countFarmersByState(String state) {
        String sql = "SELECT COUNT(*) FROM users WHERE user_type = 'FARMER' AND state_name = ?";
        try {
            Integer count = jdbcTemplate.queryForObject(sql, Integer.class, state);
            return count != null ? count : 0;
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * Raw JDBC: update soil pH directly (bypassing JPA for demo).
     */
    public int updateSoilPhRaw(Long soilId, double newPh) {
        if (newPh < 0 || newPh > 14)
            throw new InvalidSoilDataException("phLevel", newPh, "pH must be 0–14");
        String sql = "UPDATE soils SET ph_level = ? WHERE id = ?";
        try {
            return jdbcTemplate.update(sql, newPh, soilId);
        } catch (Exception e) {
            throw new InvalidSoilDataException("Raw pH update failed: " + e.getMessage());
        }
    }

    /**
     * Raw JDBC: fetch sensor readings above a threshold.
     */
    public List<Map<String, Object>> getHighMoistureReadings(double threshold) {
        String sql = "SELECT sensor_id, primary_reading_value, captured_at " +
                     "FROM sensor_data WHERE sensor_type = 'SOIL_MOISTURE' " +
                     "AND primary_reading_value >= ? ORDER BY captured_at DESC";
        try {
            return jdbcTemplate.queryForList(sql, threshold);
        } catch (Exception e) {
            return List.of();
        }
    }
}
