package com.example.warehousesystem.service;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Service
public class ExportTemplateService {

    /**
     * Tạo file Excel mẫu cho WMS-32 (Export by Excel)
     * Trả về byte[] để controller trả về client.
     */
    public byte[] createExportExcelTemplate() {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Export Template");

            // Header style (bold)
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);

            // Header row
            Row header = sheet.createRow(0);
            String[] headers = new String[] { "skuCode", "quantity", "note" };
            for (int c = 0; c < headers.length; c++) {
                Cell cell = header.createCell(c);
                cell.setCellValue(headers[c]);
                cell.setCellStyle(headerStyle);
            }

            // Example row 1
            Row example1 = sheet.createRow(1);
            example1.createCell(0).setCellValue("SKU001");
            example1.createCell(1).setCellValue(10);
            example1.createCell(2).setCellValue("Xuất cho đơn bán hàng A");

            // Example row 2
            Row example2 = sheet.createRow(2);
            example2.createCell(0).setCellValue("SKU002");
            example2.createCell(1).setCellValue(5);
            example2.createCell(2).setCellValue("");

            // Gợi ý: set autofilter and autosize
            sheet.setAutoFilter(new org.apache.poi.ss.util.CellRangeAddress(0, 0, 0, headers.length - 1));
            for (int c = 0; c < headers.length; c++) {
                sheet.autoSizeColumn(c);
            }

            workbook.write(out);
            return out.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Lỗi tạo file mẫu Excel", e);
        }
    }
}
