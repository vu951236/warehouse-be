package com.example.warehousesystem.dto.request;

import lombok.Data;

import java.time.LocalDate;

@Data
public class ImportOrderSearchRequest {
    private String source;      // factory, return
    private String status;      // draft, confirmed, cancelled
    private String createdBy;   // username
    private LocalDate startDate;
    private LocalDate endDate;
}
