package com.example.warehousesystem.dto.response;

import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
// Response tổng hợp Dashboard Nhập kho
public class ImportKpiResponse {
    // KPI Cards
    private Long totalImportOrders;   // số đơn nhập confirmed
    private Long totalItemsImported;  // tổng số item nhập
    private Long itemsFromFactory;    // item nhập từ nhà máy
    private Long itemsFromReturn;     // item nhập từ hoàn trả
}
