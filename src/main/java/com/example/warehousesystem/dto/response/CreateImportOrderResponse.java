package com.example.warehousesystem.dto.response;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateImportOrderResponse {
    private Integer id;
    private String source;
    private String status;
    private String note;
    private String createdBy;
    private LocalDateTime createdAt;
    private List<Detail> details;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Detail {
        private Integer skuId;
        private String skuCode;
        private String skuName;
        private Integer quantity;
        private Integer receivedQuantity;
    }
}
