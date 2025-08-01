package com.example.warehousesystem.dto.request;

import com.example.warehousesystem.entity.ImportOrder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder// Tìm kiếm đơn nhập
public class ImportOrderSearchRequest {
    private ImportOrder.Source source;     // factory, returnGoods
    private ImportOrder.Status status;     // draft, confirmed, cancelled
    private String createdBy;              // username
    private LocalDate startDate;
    private LocalDate endDate;
}
