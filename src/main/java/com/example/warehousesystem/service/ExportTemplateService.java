package com.example.warehousesystem.service;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;

@Service
public class ExportTemplateService {

    public ByteArrayResource generateExportExcelTemplate() {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Export Template");

            // Tạo header row
            Row headerRow = sheet.createRow(0);

            Cell skuCell = headerRow.createCell(0);
            skuCell.setCellValue("skuCode");

            Cell quantityCell = headerRow.createCell(1);
            quantityCell.setCellValue("quantity");

            // Style cho header
            CellStyle headerStyle = workbook.createCellStyle();
            Font font = workbook.createFont();
            font.setBold(true);
            headerStyle.setFont(font);

            skuCell.setCellStyle(headerStyle);
            quantityCell.setCellStyle(headerStyle);

            // Auto size columns
            sheet.autoSizeColumn(0);
            sheet.autoSizeColumn(1);

            // Ghi ra file
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);

            return new ByteArrayResource(out.toByteArray());

        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi tạo template Excel", e);
        }
    }
}
