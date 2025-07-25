package com.example.warehousesystem.dto.response;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WeatherResponse {
    private String warehouseName;
    private String location;
    private String temperature;
    private String weatherStatus;
    private String humidity;
    private String wind;
}
