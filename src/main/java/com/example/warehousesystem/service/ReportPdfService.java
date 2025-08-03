package com.example.warehousesystem.service;

import com.example.warehousesystem.dto.request.StorageStatusRequest;
import com.example.warehousesystem.dto.response.*;
import com.example.warehousesystem.repository.ExportOrderRepository;
import com.example.warehousesystem.repository.ImportOrderRepository;
import com.example.warehousesystem.repository.SKURepository;
import com.example.warehousesystem.repository.WarehouseRepository;
import com.example.warehousesystem.mapper.*;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.properties.TextAlignment;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReportPdfService {

    private final ImportOrderRepository importOrderRepository;
    private final ExportOrderRepository exportOrderRepository;
    private final SKURepository skuRepository;
    private final WarehouseRepository warehouseRepository;

    public byte[] generateWarehouseReportPdf(StorageStatusRequest request) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(outputStream);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);

        // Tiêu đề
        document.add(new Paragraph("📦 Báo Cáo Kho Tổng Hợp")
                .setBold()
                .setFontSize(18)
                .setTextAlignment(TextAlignment.CENTER));
        document.add(new Paragraph(" "));

        // 1. Tổng kết Nhập - Xuất
        document.add(new Paragraph("📈 Thống kê nhập - xuất:").setBold());
        List<Object[]> importStats = importOrderRepository.getImportStatistics(
                request.getWarehouseId(),
                request.getStartDate(),
                request.getEndDate()
        );
        List<Object[]> exportStats = exportOrderRepository.getExportStatistics(
                request.getWarehouseId(),
                request.getStartDate(),
                request.getEndDate()
        );

        for (int i = 0; i < importStats.size(); i++) {
            Object[] imp = importStats.get(i);
            Object[] exp = exportStats.size() > i ? exportStats.get(i) : new Object[]{imp[0], 0L, 0L};

            OptimizationIndexResponse index = OptimizationIndexMapper.toResponse(
                    imp[0].toString(),
                    ((Number) imp[1]).longValue(),
                    ((Number) imp[2]).longValue(),
                    ((Number) exp[1]).longValue(),
                    ((Number) exp[2]).longValue()
            );

            document.add(new Paragraph("Ngày " + index.getDate()
                    + " - Nhập: " + index.getTotalImportItems()
                    + ", Xuất: " + index.getTotalExportItems()
                    + ", Tối ưu: " + index.getOptimizationRate() + "%"));
        }

        document.add(new Paragraph(" "));

        // 2. Tỉ lệ loại hàng
        document.add(new Paragraph("📊 Tỉ lệ loại hàng đang lưu kho:").setBold());
        List<SkuTypeRatioChartResponse> skuRatios = skuRepository.getCurrentStockRatioChart()
                .stream()
                .map(obj -> SkuTypeRatioChartMapper.toResponse(
                        (String) obj[0],
                        ((Number) obj[1]).longValue()))
                .collect(Collectors.toList());

        for (SkuTypeRatioChartResponse sku : skuRatios) {
            document.add(new Paragraph("• " + sku.getSkuName() + ": " + sku.getTotalQuantity() + " sản phẩm"));
        }

        document.add(new Paragraph(" "));

        // 3. Tình trạng sức chứa
        document.add(new Paragraph("🏗️ Tình trạng sức chứa:").setBold());
        WarehouseStorageStatusProjection raw = warehouseRepository.getWarehouseStorageStatusById(request.getWarehouseId());
        if (raw != null) {
            StorageStatusResponse status = StorageStatusMapper.toResponse(
                    raw.getWarehouseName(),
                    raw.getUsedCapacity(),
                    raw.getShelfCount(),
                    raw.getTotalBinCount(),
                    raw.getBinCapacity()
            );

            document.add(new Paragraph("Kho: " + status.getWarehouseName()));
            document.add(new Paragraph("• Dung lượng đã dùng: " + status.getUsedPercentage() + "%"));
            document.add(new Paragraph("• Dung lượng còn trống: " + status.getFreePercentage() + "%"));
        }

        document.close();
        return outputStream.toByteArray();
    }
}
