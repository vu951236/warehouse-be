package com.example.warehousesystem.service;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Service
public class ImportTemplateService {

    /**
     * Tạo file Excel mẫu với 2 cột: SKU Code, Quantity
     */
    public ByteArrayResource generateTemplate() throws IOException {
        String[] columns = {"SKU Code", "Quantity"};

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Import Template");

            // Header
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < columns.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(columns[i]);
            }

            // Ví dụ dòng mẫu (tùy thích)
            Row sample = sheet.createRow(1);
            sample.createCell(0).setCellValue("SKU-001");
            sample.createCell(1).setCellValue(10);

            // Auto size
            for (int i = 0; i < columns.length; i++) sheet.autoSizeColumn(i);

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            return new ByteArrayResource(out.toByteArray());
        }
    }
}