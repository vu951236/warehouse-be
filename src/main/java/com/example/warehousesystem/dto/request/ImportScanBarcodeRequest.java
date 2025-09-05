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
// Nhập kho bằng cách quét mã vạch (QR code)
public class ImportScanBarcodeRequest {
    private List<ScanBarcodeDTO> scannedItems; // Danh sách mã quét
    private ImportOrder.Source source;
    private String note;       // Ghi chú
    private LocalDate importDate;  // Ngày nhập kho
    private String importOrderCode; // Mã đơn nhập
}
