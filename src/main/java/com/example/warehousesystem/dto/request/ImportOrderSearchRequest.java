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
    private String importCode;
    private String skuCode;
    private LocalDate createdAt;
}
