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
public class ImportOrderSearch2Request {
    private String importCode;
    private ImportOrder.Source source;
    private LocalDate startDate;
    private LocalDate endDate;

}
