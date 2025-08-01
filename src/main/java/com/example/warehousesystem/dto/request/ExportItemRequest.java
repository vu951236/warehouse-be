package com.example.warehousesystem.dto.request;

import lombok.*;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
//Xuất hàng vật phẩm
public class ExportItemRequest {
    private Integer exportOrderId;
    private List<String> barcodes;         // Danh sách mã barcode cần xuất
    private Integer userId;
}
