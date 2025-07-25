package com.example.warehousesystem.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ImportOrderResponse {
    private Integer id;
    private String source;
    private String status;
    private String createdBy;
    private LocalDateTime createdAt;
    private String note;
}
