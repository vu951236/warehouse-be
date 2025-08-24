package com.example.warehousesystem.service;

import com.example.warehousesystem.dto.response.ExportOrderBoardResponse;
import com.example.warehousesystem.entity.ExportOrderDetail;
import com.example.warehousesystem.repository.ExportOrderDetailRepository;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExportExcelExportService {

    private final ExportOrderDetailRepository exportOrderDetailRepository;

    public ByteArrayInputStream exportAllExportOrdersToExcel() throws IOException {
        List<ExportOrderDetail> details = exportOrderDetailRepository.findAll();

        if (details.isEmpty()) {
            return new ByteArrayInputStream(new byte[0]);
        }

        // Gom nhóm theo ExportOrder
        Map<Long, List<ExportOrderDetail>> groupedByOrder = details.stream()
                .collect(Collectors.groupingBy(d -> Long.valueOf(d.getExportOrder().getId())));

        List<ExportOrderBoardResponse> responses = new ArrayList<>();
        for (Map.Entry<Long, List<ExportOrderDetail>> entry : groupedByOrder.entrySet()) {
            List<ExportOrderDetail> orderDetails = entry.getValue();
            ExportOrderDetail first = orderDetails.get(0);

            String allSkuCodes = orderDetails.stream()
                    .map(d -> d.getSku().getSkuCode())
                    .distinct()
                    .collect(Collectors.joining(", "));

            int totalQuantity = orderDetails.stream()
                    .mapToInt(ExportOrderDetail::getQuantity)
                    .sum();

            ExportOrderBoardResponse merged = ExportOrderBoardResponse.builder()
                    .id(Long.valueOf(first.getExportOrder().getId()))
                    .exportCode(first.getExportOrder().getExportCode())
                    .note(first.getExportOrder().getNote()) // note của exportOrder
                    .createdAt(first.getExportOrder().getCreatedAt())
                    .skuCode(allSkuCodes)
                    .quantity(totalQuantity)
                    .build();

            responses.add(merged);
        }

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");

        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Export Orders");

            // Header
            Row header = sheet.createRow(0);
            String[] columns = {"ID", "Mã đơn xuất", "Note", "Ngày xuất", "SKU Codes", "Số lượng"};
            for (int i = 0; i < columns.length; i++) {
                header.createCell(i).setCellValue(columns[i]);
            }

            // Data
            int rowIdx = 1;
            for (ExportOrderBoardResponse item : responses) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(item.getId());
                row.createCell(1).setCellValue(item.getExportCode());
                row.createCell(2).setCellValue(item.getNote() != null ? item.getNote() : "");
                row.createCell(3).setCellValue(
                        item.getCreatedAt() != null ? item.getCreatedAt().format(dateFormatter) : ""
                );
                row.createCell(4).setCellValue(item.getSkuCode());
                row.createCell(5).setCellValue(item.getQuantity() != null ? item.getQuantity() : 0);
            }

            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        }
    }
}
