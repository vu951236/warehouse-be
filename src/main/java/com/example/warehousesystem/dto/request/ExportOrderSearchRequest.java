package com.example.warehousesystem.dto.request;

import lombok.Data;
import java.time.LocalDate;

@Data
//Tìm kiếm đơn xuất
public class ExportOrderSearchRequest {
    private String source;      // manual, haravan
    private String status;      // draft, confirmed, cancelled
    private String createdBy;   // username
    private LocalDate startDate;
    private LocalDate endDate;
}
