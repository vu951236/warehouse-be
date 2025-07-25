package com.example.warehousesystem.dto.request;

import com.example.warehousesystem.dto.ScanBarcodeDTO;
import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ImportScanBarcodeRequest {
    private Integer importOrderId;
    private List<ScanBarcodeDTO> scannedItems;
    private Integer userId;
}
