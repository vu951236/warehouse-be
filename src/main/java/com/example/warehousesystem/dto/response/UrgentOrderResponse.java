package com.example.warehousesystem.dto.response;

import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UrgentOrderResponse {
    private Integer exportOrderId;
    private String exportCode;
    private String destination;
    private String status;
    private String createdBy;
    private LocalDateTime createdAt;
    private String note;
}
