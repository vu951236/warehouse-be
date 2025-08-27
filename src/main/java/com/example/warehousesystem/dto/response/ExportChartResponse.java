package com.example.warehousesystem.dto.response;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ExportChartResponse {
    private LocalDate date;       // Ngày xuất
    private Long manualQuantity;  // Tổng số lượng source = manual
    private Long haravanQuantity; // Tổng số lượng source = haravan
}
