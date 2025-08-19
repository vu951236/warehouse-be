package com.example.warehousesystem.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AllExportOrderResponse {
    private Integer id;  // Mã đơn xuất
    private String exportCode;  // Mã đơn xuất
    private String skuCode;     // Mã SKU
    private String productName; // Tên sản phẩm

    @JsonFormat(pattern = "yyyy/MM/dd")
    private LocalDateTime exportDate; // Ngày xuất

    private Integer quantity;   // Số lượng
}
