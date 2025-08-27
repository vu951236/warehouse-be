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
@Builder
public class SearchExportOrder2Request {
    private String exportCode;   // mã đơn xuất
    private ExportOrder.Source source;      // mã SKU
    private LocalDate startDate;
    private LocalDate endDate;

}

