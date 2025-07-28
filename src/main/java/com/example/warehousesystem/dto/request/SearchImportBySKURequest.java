package com.example.warehousesystem.dto.request;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
//Xem thông tin nhập hàng theo SKU
public class SearchImportBySKURequest {
    private String skuCode;
    private String fromDate; // yyyy-MM-dd
    private String toDate;
}
