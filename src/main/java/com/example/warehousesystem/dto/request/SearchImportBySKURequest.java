package com.example.warehousesystem.dto.request;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SearchImportBySKURequest {
    private String skuCode;
    private String fromDate; // yyyy-MM-dd
    private String toDate;
}
