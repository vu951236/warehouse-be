package com.example.warehousesystem.dto.request;

import com.example.warehousesystem.entity.ImportOrder;
import lombok.Data;

import java.time.LocalDate;

@Data
// Tìm kiếm đơn nhập
public class ImportOrderSearchRequest {
    private ImportOrder.Source source;     // factory, returnGoods
    private ImportOrder.Status status;     // draft, confirmed, cancelled
    private String createdBy;              // username
    private LocalDate startDate;
    private LocalDate endDate;
}
