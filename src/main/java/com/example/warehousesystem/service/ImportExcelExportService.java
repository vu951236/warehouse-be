package com.example.warehousesystem.service;

import com.example.warehousesystem.dto.response.ImportOrderBoardResponse;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ImportExcelExportService {

    private final ImportOrderService importOrderService;

    public ByteArrayResource exportImportOrders() throws IOException {
        List<ImportOrderBoardResponse> orders = importOrderService.getAllImportOrderDetailsMergedWithSkuList();

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Import Orders");

            // Header
            String[] columns = {"ID", "Import Code", "SKU Codes", "SKU Names", "Created At", "Quantity"};
            Row headerRow = sheet.createRow(0);

            for (int i = 0; i < columns.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(columns[i]);
            }

            // Data
            int rowIdx = 1;
            for (ImportOrderBoardResponse order : orders) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(order.getId());
                row.createCell(1).setCellValue(order.getImportCode());
                row.createCell(2).setCellValue(order.getSkuCode());
                row.createCell(3).setCellValue(order.getSkuName());
                row.createCell(4).setCellValue(order.getCreatedAt().toString());
                row.createCell(5).setCellValue(order.getQuantity());
            }

            // Auto size
            for (int i = 0; i < columns.length; i++) {
                sheet.autoSizeColumn(i);
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            return new ByteArrayResource(out.toByteArray());
        }
    }
}
