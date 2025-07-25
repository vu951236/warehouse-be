package com.example.warehousesystem.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExcelItemDTO {
    private String barcode;
    private Integer boxId;
    private Integer importOrderDetailId;
}

