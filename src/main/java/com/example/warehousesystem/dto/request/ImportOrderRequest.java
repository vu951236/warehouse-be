package com.example.warehousesystem.dto.request;

import lombok.Data;
import java.util.List;

@Data
public class ImportOrderRequest {
    private String source;
    private String note;

    private List<ImportOrderDetailRequest> details;

    @Data
    public static class ImportOrderDetailRequest {
        private String skuCode;
        private Integer quantity;
    }
}
