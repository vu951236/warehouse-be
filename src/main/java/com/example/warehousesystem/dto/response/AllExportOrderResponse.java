package com.example.warehousesystem.dto.response;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AllExportOrderResponse {
    private String exportCode;  // Mã đơn xuất
    private String skuCode;     // Mã SKU
    private String productName; // Tên sản phẩm
    private LocalDateTime exportDate; // Ngày xuất
    private Integer quantity;   // Số lượng
}
