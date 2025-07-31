package com.example.warehousesystem.dto.request;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateImportOrderRequest {
    private String source;
    private String note;
    private List<Detail> details;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Detail {
        private Integer skuId;
        private Integer quantity;
    }
}
