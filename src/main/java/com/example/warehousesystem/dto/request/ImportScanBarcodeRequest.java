package com.example.warehousesystem.dto.request;

import com.example.warehousesystem.dto.ScanBarcodeDTO;
import com.example.warehousesystem.entity.ImportOrder;
import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
// Nhập kho bằng cách quét mã vạch (QR code)
public class ImportScanBarcodeRequest {
    private List<ScanBarcodeDTO> scannedItems; // Danh sách mã quét
    private ImportOrder.Source source;
    private Integer userId;                    // Người nhập
    private String note;                       // Ghi chú
}
