package com.example.warehousesystem.dto.request;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UrgentOrderRequest {
    private String status;           // draft / confirmed / cancelled
    private Integer maxResults;      // Giới hạn số đơn cần lấy
}
