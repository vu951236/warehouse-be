package com.example.warehousesystem.dto.request;

import lombok.*;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
// Xuất hàng vật phẩm theo lựa chọn
public class ExportItemRequest {
    private List<ExportQueueDTO> items; // Danh sách SKU + số lượng

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class ExportQueueDTO {
        private String sku;
        private Integer quantity;
    }
}
