package com.example.warehousesystem.dto.request;

import com.example.warehousesystem.dto.ScanBarcodeDTO;
import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
//Nhập kho bằng mã QR
public class ImportScanBarcodeRequest {
    private Integer importOrderId;
    private List<ScanBarcodeDTO> scannedItems;// file ScanBarcodeDTO
    private Integer userId;
}
