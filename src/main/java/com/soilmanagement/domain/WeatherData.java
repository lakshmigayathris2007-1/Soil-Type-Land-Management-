package com.soilmanagement.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "weather_data")
public class WeatherData implements Analyzable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 30)
    private String stationCode;

    @Column(nullable = false)
    private LocalDateTime recordedAt;

    @Column(nullable = false)
    private Double temperatureCelsius;

    @Column(nullable = false)
    private Double humidityPercent;

    @Column(nullable = false)
    private Double rainfallMm;

    @Column
    private Double windSpeedKmPerHour;

    @Column
    private Double windDirectionDegrees;

    @Column
    private Double solarRadiationWPerM2;

    @Column
    private Double evapotranspirationMm;

    @Column
    private Double dewPointCelsius;

    @Column(length = 50)
    private String weatherCondition;

    @Column(length = 100)
    private String locationDescription;

    @Column
    private Double atmosphericPressureHpa;

    public WeatherData() {}

    @Override
    public double calculateIndex() {
        double score = 0;
        if (temperatureCelsius >= 20 && temperatureCelsius <= 30) score += 35;
        else score += Math.max(0, 35 - Math.abs(temperatureCelsius - 25) * 2);
        if (humidityPercent >= 50 && humidityPercent <= 70) score += 30;
        else score += Math.max(0, 30 - Math.abs(humidityPercent - 60) * 0.5);
        if (rainfallMm >= 2 && rainfallMm <= 10) score += 20;
        else if (rainfallMm == 0) score += 10;
        else score += Math.max(0, 20 - (rainfallMm - 10) * 0.5);
        if (windSpeedKmPerHour != null && windSpeedKmPerHour < 20) score += 15;
        return Math.min(100, Math.round(score * 10.0) / 10.0);
    }

    @Override
    public String analyzeCondition() {
        double idx = calculateIndex();
        if (idx >= 75) return "FAVOURABLE";
        if (idx >= 50) return "ACCEPTABLE";
        return "UNFAVOURABLE";
    }

    @Override
    public boolean meetsStandard(String standardCode) {
        if ("FROST_RISK".equals(standardCode))  return temperatureCelsius < 2.0;
        if ("DROUGHT_RISK".equals(standardCode)) return rainfallMm < 0.5 && humidityPercent < 30;
        return false;
    }

    public double estimatePotentialEt() {
        if (solarRadiationWPerM2 == null) return evapotranspirationMm != null ? evapotranspirationMm : 0;
        return 0.0023 * (temperatureCelsius + 17.8)
                * Math.sqrt(Math.abs(temperatureCelsius - (dewPointCelsius != null ? dewPointCelsius : 0)))
                * solarRadiationWPerM2 * 0.0864;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getStationCode() { return stationCode; }
    public void setStationCode(String v) { this.stationCode = v; }
    public LocalDateTime getRecordedAt() { return recordedAt; }
    public void setRecordedAt(LocalDateTime v) { this.recordedAt = v; }
    public Double getTemperatureCelsius() { return temperatureCelsius; }
    public void setTemperatureCelsius(Double v) { this.temperatureCelsius = v; }
    public Double getHumidityPercent() { return humidityPercent; }
    public void setHumidityPercent(Double v) { this.humidityPercent = v; }
    public Double getRainfallMm() { return rainfallMm; }
    public void setRainfallMm(Double v) { this.rainfallMm = v; }
    public Double getWindSpeedKmPerHour() { return windSpeedKmPerHour; }
    public void setWindSpeedKmPerHour(Double v) { this.windSpeedKmPerHour = v; }
    public Double getWindDirectionDegrees() { return windDirectionDegrees; }
    public void setWindDirectionDegrees(Double v) { this.windDirectionDegrees = v; }
    public Double getSolarRadiationWPerM2() { return solarRadiationWPerM2; }
    public void setSolarRadiationWPerM2(Double v) { this.solarRadiationWPerM2 = v; }
    public Double getEvapotranspirationMm() { return evapotranspirationMm; }
    public void setEvapotranspirationMm(Double v) { this.evapotranspirationMm = v; }
    public Double getDewPointCelsius() { return dewPointCelsius; }
    public void setDewPointCelsius(Double v) { this.dewPointCelsius = v; }
    public String getWeatherCondition() { return weatherCondition; }
    public void setWeatherCondition(String v) { this.weatherCondition = v; }
    public String getLocationDescription() { return locationDescription; }
    public void setLocationDescription(String v) { this.locationDescription = v; }
    public Double getAtmosphericPressureHpa() { return atmosphericPressureHpa; }
    public void setAtmosphericPressureHpa(Double v) { this.atmosphericPressureHpa = v; }
}
