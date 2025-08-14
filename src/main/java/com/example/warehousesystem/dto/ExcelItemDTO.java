package com.example.warehousesystem.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExcelItemDTO {
    private String skuCode;
    private Integer quantity;
    private String source;
    private String note;
}
