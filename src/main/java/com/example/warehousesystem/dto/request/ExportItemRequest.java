package com.example.warehousesystem.dto.request;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ExportItemRequest {
    private Integer exportOrderId;
    private Integer exportOrderDetailId;
    private String barcode;
    private Integer userId;
}
