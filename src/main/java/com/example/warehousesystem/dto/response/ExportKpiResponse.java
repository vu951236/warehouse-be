package com.example.warehousesystem.dto.response;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ExportKpiResponse {
    private Long totalConfirmedOrders;   // KPI 1
    private Long totalConfirmedQuantity; // KPI 2

}
