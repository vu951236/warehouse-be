package com.example.warehousesystem.dto.request;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
//Ưu tiên đơn hàng gấp
public class UrgentOrderRequest {
    private String status;           // draft / confirmed / cancelled
    private Integer maxResults;      // Giới hạn số đơn cần lấy
}
