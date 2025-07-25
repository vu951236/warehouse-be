package com.example.warehousesystem.dto.request;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ImportItemRequest {
    private Integer importOrderId;
    private Integer importOrderDetailId;
    private String barcode;
    private Integer boxId;
    private Integer userId;
}
