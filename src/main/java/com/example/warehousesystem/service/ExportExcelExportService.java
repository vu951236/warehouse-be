package com.example.warehousesystem.service;

import com.example.warehousesystem.dto.request.SearchExportBySKURequest;
import com.example.warehousesystem.dto.response.SearchExportBySKUResponse;
import com.example.warehousesystem.mapper.SearchExportBySKUMapper;
import com.example.warehousesystem.repository.ExportOrderDetailRepository;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExportExcelExportService {

    private final ExportOrderDetailRepository exportOrderDetailRepository;

    public ByteArrayInputStream exportExportHistoryBySku(SearchExportBySKURequest request) throws IOException {
        LocalDateTime fromDate = request.getFromDate() != null ?
                LocalDateTime.parse(request.getFromDate() + "T00:00:00") : null;
        LocalDateTime toDate = request.getToDate() != null ?
                LocalDateTime.parse(request.getToDate() + "T23:59:59") : null;

        List<Object[]> resultSet = exportOrderDetailRepository.findExportDetailsBySku(
                request.getSkuCode(), fromDate, toDate
        );

        List<SearchExportBySKUResponse> responses = resultSet.stream()
                .map(row -> SearchExportBySKUMapper.toExportBySkuResponse(
                        (com.example.warehousesystem.entity.ExportOrder) row[1],
                        (com.example.warehousesystem.entity.ExportOrderDetail) row[0],
                        (String) row[2]
                ))
                .toList();

        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Export History");

            // Header
            Row header = sheet.createRow(0);
            String[] columns = {"Mã đơn xuất", "Ngày xuất", "Số lượng", "Trạng thái", "Người tạo", "Kho"};
            for (int i = 0; i < columns.length; i++) {
                header.createCell(i).setCellValue(columns[i]);
            }

            // Data
            int rowIdx = 1;
            for (SearchExportBySKUResponse item : responses) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(item.getExportOrderId());
                row.createCell(1).setCellValue(item.getExportDate());
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
