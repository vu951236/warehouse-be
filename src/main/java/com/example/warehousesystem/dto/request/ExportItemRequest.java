package com.example.warehousesystem.dto.request;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
//Xuất hàng vật phẩm
public class ExportItemRequest {
    private Integer exportOrderId;
    private Integer exportOrderDetailId;
    private String barcode;
    private Integer userId;
}
