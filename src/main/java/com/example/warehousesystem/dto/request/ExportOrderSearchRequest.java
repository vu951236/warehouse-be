package com.example.warehousesystem.dto.request;

import com.example.warehousesystem.entity.ExportOrder;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder//Tìm kiếm đơn xuất
public class ExportOrderSearchRequest {
    private ExportOrder.Source source;      // manual, haravan
    private ExportOrder.Status status;      // draft, confirmed, cancelled
    private String createdBy;   // username
    private LocalDate startDate;
    private LocalDate endDate;
}
