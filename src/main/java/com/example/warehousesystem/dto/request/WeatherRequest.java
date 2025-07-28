package com.example.warehousesystem.dto.request;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
//Thời tiết
public class WeatherRequest {
    private Integer warehouseId; // tuỳ chọn: nếu null thì lấy toàn bộ
}
