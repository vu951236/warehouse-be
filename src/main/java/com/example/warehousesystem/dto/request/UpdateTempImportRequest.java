package com.example.warehousesystem.dto.request;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateTempImportRequest {
    private Long id;
    private String skuCode;
    private Integer quantity;
    private String source;
    private String note;
}
