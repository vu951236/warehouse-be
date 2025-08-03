package com.example.warehousesystem.service;

import com.example.warehousesystem.dto.request.ScanBarcodeRequest;
import com.example.warehousesystem.dto.response.ScanBarcodeResponse;

public interface BarcodeScanService {
    ScanBarcodeResponse scanBarcode(ScanBarcodeRequest request);
}
