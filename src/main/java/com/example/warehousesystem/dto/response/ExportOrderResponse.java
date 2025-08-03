package com.example.warehousesystem.dto.response;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
//Đơn xuất
public class ExportOrderResponse {
    private Integer id;
    private String exportCode;
    private String destination;
    private String source;
    private String status;
    private String createdBy;
    private LocalDateTime createdAt;
    private String note;
}
