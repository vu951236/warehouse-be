package com.example.warehousesystem.dto.request;

import com.example.warehousesystem.dto.ScanBarcodeDTO;
import com.example.warehousesystem.entity.ImportOrder;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ImportScanBarcodeRequest {
    private List<ScanBarcodeDTO> scannedItems;
    private ImportOrder.Source source;
    private String note;
    private LocalDate importDate;
}
