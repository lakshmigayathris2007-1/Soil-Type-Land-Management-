package com.soilmanagement.repository;

import com.soilmanagement.domain.SensorData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SensorDataRepository extends JpaRepository<SensorData, Long> {
    List<SensorData> findBySensorId(String sensorId);
    List<SensorData> findBySensorType(String sensorType);
    List<SensorData> findByDeployedOnLand_Id(Long landId);

    @Query("SELECT sd FROM SensorData sd WHERE sd.batteryLevelPercent < :threshold")
    List<SensorData> findLowBatterySensors(@Param("threshold") Double threshold);
}
