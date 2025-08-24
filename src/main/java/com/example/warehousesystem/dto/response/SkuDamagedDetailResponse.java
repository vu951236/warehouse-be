package com.example.warehousesystem.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SkuDamagedDetailResponse {
    private Integer id;
    private String skuCode;
    private String name;
    private String size;
    private String color;
    private String type;
    private Float unitVolume;

    @JsonFormat(pattern = "yyyy/MM/dd")
    private LocalDateTime createdAt;

    private Long damagedItemCount;
    private String storageLocation;
    private List<DamagedItemDetail> damagedItems;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class DamagedItemDetail {
        private String barcode;
        private String note;
        private String boxCode;

        @JsonFormat(pattern = "yyyy/MM/dd")
        private LocalDateTime transferredAt;
    }
}
