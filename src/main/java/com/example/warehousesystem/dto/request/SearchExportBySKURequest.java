package com.example.warehousesystem.dto.request;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
//Xem thông tin xuất hàng theo SKU
public class SearchExportBySKURequest {
    private String skuCode;
    private String fromDate; // yyyy-MM-dd
    private String toDate;   // yyyy-MM-dd
}
