package com.example.warehousesystem.service;

import com.example.warehousesystem.dto.request.*;
import com.example.warehousesystem.dto.response.ImportItemsResponse;
import com.example.warehousesystem.dto.response.ImportOrderResponse;
import com.example.warehousesystem.dto.response.ScanBarcodeResponse;
import com.example.warehousesystem.dto.response.SearchImportBySKUResponse;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

public interface ImportOrderService {

    List<ImportOrderResponse> searchImportOrders(ImportOrderSearchRequest request);

    ImportItemsResponse importSingleItem(ImportScanBarcodeRequest request);

    ImportItemsResponse importFromExcel(ImportExcelItemRequest request);

    ByteArrayOutputStream generateImportTemplate() throws IOException;

    ImportItemsResponse importByBarcode(ImportScanBarcodeRequest request);

    List<SearchImportBySKUResponse> getImportHistoryBySKU(SearchImportBySKURequest request);

}

