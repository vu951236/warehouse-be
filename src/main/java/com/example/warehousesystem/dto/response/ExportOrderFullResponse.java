package com.example.warehousesystem.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ExportOrderFullResponse {
    private Integer id;
    private String exportCode;
    private String source;
    private String status;
    private String createdBy;

    @JsonFormat(pattern = "yyyy/MM/dd")
    private LocalDateTime createdAt;

    private String note;
    private List<ExportOrderDetailItem> details;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class ExportOrderDetailItem {
        private Integer id;
        private String skuCode;
        private String skuName;
        private String size;
        private String color;
        private String type;
        private Double unitVolume;
        private Integer quantity;
    }
}
