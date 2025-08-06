package com.example.warehousesystem.service;

import com.example.warehousesystem.dto.request.SearchImportBySKURequest;
import com.example.warehousesystem.dto.response.SearchImportBySKUResponse;
import com.example.warehousesystem.mapper.SearchImportBySKUMapper;
import com.example.warehousesystem.repository.ImportOrderDetailRepository;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ImportExcelExportService {

    private final ImportOrderDetailRepository importOrderDetailRepository;

    public ByteArrayInputStream exportImportHistoryBySku(SearchImportBySKURequest request) throws IOException {
        LocalDateTime fromDate = request.getFromDate() != null ?
                LocalDateTime.parse(request.getFromDate() + "T00:00:00") : null;
        LocalDateTime toDate = request.getToDate() != null ?
                LocalDateTime.parse(request.getToDate() + "T23:59:59") : null;

        List<Object[]> resultSet = importOrderDetailRepository.findImportDetailsBySku(
                request.getSkuCode(), fromDate, toDate
        );

        List<SearchImportBySKUResponse> responses = resultSet.stream()
                .map(row -> SearchImportBySKUMapper.toImportBySkuResponse(
                        (com.example.warehousesystem.entity.ImportOrder) row[1],
                        (com.example.warehousesystem.entity.ImportOrderDetail) row[0],
                        (String) row[2]
                ))
                .toList();

        // Tạo Excel
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Import History");

            // Header
            Row header = sheet.createRow(0);
            String[] columns = {"Mã đơn nhập", "Ngày nhập", "Số lượng", "Trạng thái", "Người tạo", "Kho"};
            for (int i = 0; i < columns.length; i++) {
                Cell cell = header.createCell(i);
                cell.setCellValue(columns[i]);
            }

            // Data
            int rowIdx = 1;
            for (SearchImportBySKUResponse item : responses) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(item.getImportOrderId());
                row.createCell(1).setCellValue(item.getImportDate());
                row.createCell(2).setCellValue(item.getQuantity());
                row.createCell(3).setCellValue(item.getStatus());
                row.createCell(4).setCellValue(item.getCreatedBy());
                row.createCell(5).setCellValue(item.getWarehouseName());
            }

            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        }
    }
}
