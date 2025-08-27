package com.example.warehousesystem.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SearchExportOrderRequest {
    private String exportCode;   // mã đơn xuất
    private String skuCode;      // mã SKU
    private LocalDate startDate;
    private LocalDate endDate;

}

