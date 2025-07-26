package com.example.warehousesystem.dto.response;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SearchExportBySKUResponse {
    private Integer exportOrderId;
    private String exportDate;
    private Integer quantity;
    private String status;
    private String createdBy;
    private String warehouseName;
}
