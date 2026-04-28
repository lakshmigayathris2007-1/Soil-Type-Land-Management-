package com.soilmanagement.repository;

import com.soilmanagement.domain.WeatherData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WeatherDataRepository extends JpaRepository<WeatherData, Long> {
    List<WeatherData> findByStationCode(String stationCode);
    List<WeatherData> findByWeatherCondition(String condition);

    @Query("SELECT wd FROM WeatherData wd WHERE wd.temperatureCelsius < :minTemp")
    List<WeatherData> findFrostRiskReadings(@Param("minTemp") Double minTemp);
}
