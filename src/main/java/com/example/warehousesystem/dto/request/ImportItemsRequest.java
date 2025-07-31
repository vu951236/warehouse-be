package com.example.warehousesystem.dto.request;

import com.example.warehousesystem.entity.ImportOrder;
import lombok.*;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ImportItemsRequest {
    private Integer userId;
    private ImportOrder.Source source;
    private String note;

    private List<ImportItemDetail> items;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ImportItemDetail {
        private String barcode;
        private Integer skuId;
        private Integer quantity;
    }
}

