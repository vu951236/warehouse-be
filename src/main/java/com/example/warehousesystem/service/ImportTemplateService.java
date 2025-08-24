package com.example.warehousesystem.service;

import com.example.warehousesystem.entity.SKU;
import com.example.warehousesystem.repository.SKURepository;
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
public class ImportTemplateService {

    private final SKURepository skuRepository;

    /**
     * WMS-19: Tạo file Excel mẫu có sheet nhập + sheet danh sách SKU từ DB
     */
    public ByteArrayResource generateTemplate() throws IOException {
        String[] columns = {"SKU Code", "Quantity", "Source", "Note"};

        try (Workbook workbook = new XSSFWorkbook()) {
            // ==== Sheet 1: Import Template ====
            Sheet sheet = workbook.createSheet("Import Template");

            // Header
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < columns.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(columns[i]);
            }

            // Ví dụ dòng mẫu
            Row sample = sheet.createRow(1);
            sample.createCell(0).setCellValue("SKU-001");
            sample.createCell(1).setCellValue(100);
            sample.createCell(2).setCellValue("Factory A");
            sample.createCell(3).setCellValue("Hàng demo");

            for (int i = 0; i < columns.length; i++) {
                sheet.autoSizeColumn(i);
            }

            // ==== Sheet 2: Danh sách SKU từ DB ====
            Sheet skuSheet = workbook.createSheet("SKU List");
            Row skuHeader = skuSheet.createRow(0);
            skuHeader.createCell(0).setCellValue("SKU Code");
            skuHeader.createCell(1).setCellValue("Name");
            skuHeader.createCell(2).setCellValue("Size");
            skuHeader.createCell(3).setCellValue("Color");
            skuHeader.createCell(4).setCellValue("Type");
            skuHeader.createCell(5).setCellValue("Unit Volume");

            // Lấy dữ liệu từ DB
            List<SKU> skus = skuRepository.findAll();
            int rowIdx = 1;
            for (SKU sku : skus) {
                Row row = skuSheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(sku.getSkuCode());
                row.createCell(1).setCellValue(sku.getName() != null ? sku.getName() : "");
                row.createCell(2).setCellValue(sku.getSize() != null ? sku.getSize() : "");
                row.createCell(3).setCellValue(sku.getColor() != null ? sku.getColor() : "");
                row.createCell(4).setCellValue(sku.getType() != null ? sku.getType() : "");
                row.createCell(5).setCellValue(sku.getUnitVolume() != null ? sku.getUnitVolume() : 0);
            }

            for (int i = 0; i < 6; i++) {
                skuSheet.autoSizeColumn(i);
            }

            // Xuất file
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            return new ByteArrayResource(out.toByteArray());
        }
    }
}
