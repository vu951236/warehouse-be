package com.example.warehousesystem.mapper;

import com.example.warehousesystem.dto.response.WeatherResponse;
import com.example.warehousesystem.entity.Warehouse;

public class WeatherMapper {
    public static WeatherResponse toResponse(Warehouse warehouse, String temperature, String status, String humidity, String wind) {
        return WeatherResponse.builder()
                .warehouseName(warehouse.getName())
                .location(warehouse.getLocation())
                .temperature(temperature)
                .weatherStatus(status)
                .humidity(humidity)
                .wind(wind)
                .build();
    }
}
