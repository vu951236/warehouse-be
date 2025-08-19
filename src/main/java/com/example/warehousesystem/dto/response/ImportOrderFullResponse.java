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
public class ImportOrderFullResponse {
    private Integer id;
    private String importCode;
    private String source;
    private String status;
    private String createdBy;

    @JsonFormat(pattern = "yyyy/MM/dd")
    private LocalDateTime createdAt;

    private String note;

    private List<ImportOrderDetailItem> details;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class ImportOrderDetailItem {
        private Integer id;
        private String skuCode;
        private String skuName;
        private String size;
        private String color;
        private String type;
        private Float unitVolume;
        private Integer quantity;
    }
}
