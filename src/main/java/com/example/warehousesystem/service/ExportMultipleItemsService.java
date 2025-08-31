package com.example.warehousesystem.service;

import com.example.warehousesystem.dto.request.ExportItemRequest;
import com.example.warehousesystem.dto.request.PickingRouteRequest;
import com.example.warehousesystem.dto.response.*;
import com.example.warehousesystem.entity.*;
import com.example.warehousesystem.mapper.ItemExportMapper;
import com.example.warehousesystem.repository.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.io.FileOutputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ExportMultipleItemsService {

    private final ExportOrderRepository exportOrderRepository;
    private final ExportOrderDetailRepository exportOrderDetailRepository;
    private final ItemRepository itemRepository;
    private final BoxRepository boxRepository;
    private final SKURepository skuRepository;
    private final UserRepository userRepository;
    private final PickingRouteService pickingRouteService;
    private final ExportLogRepository exportLogRepository;

    private Integer getCurrentUserId() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));
        return Math.toIntExact(user.getId());
    }

    @Transactional
    public ExportWithPickingRouteResponse exportQueuedItems(List<ExportItemRequest.ExportQueueDTO> dtos) throws JsonProcessingException {
        List<ExportItemResponse> responses = new ArrayList<>();

        // --- Kiểm tra SKU ---
        for (ExportItemRequest.ExportQueueDTO dto : dtos) {
            SKU skuEntity = skuRepository.findBySkuCode(dto.getSku())
                    .orElseThrow(() -> new RuntimeException("SKU không tồn tại: " + dto.getSku()));

            long queuedCount = itemRepository.findItemsBySku(skuEntity).stream()
                    .filter(i -> i.getStatus() == Item.Status.queued)
                    .count();

            if (queuedCount < dto.getQuantity()) {
                throw new RuntimeException("SKU " + dto.getSku() + " không đủ số lượng queued (" + queuedCount + "/" + dto.getQuantity() + ")");
            }
        }

        // --- Tạo ExportOrder ---
        String exportCode = "EX" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        User createdBy = userRepository.findById(getCurrentUserId())
                .orElseThrow(() -> new RuntimeException("User không tồn tại"));

        ExportOrder exportOrder = ExportOrder.builder()
                .exportCode(exportCode)
                .createdBy(createdBy)
                .createdAt(LocalDate.now())
                .destination("Kho chính")
                .status(ExportOrder.Status.draft)
                .source(ExportOrder.Source.manual)
                .urgent(false)
                .build();
        exportOrder = exportOrderRepository.save(exportOrder);

        // --- Xử lý từng SKU ---
        for (ExportItemRequest.ExportQueueDTO dto : dtos) {
            SKU skuEntity = skuRepository.findBySkuCode(dto.getSku()).orElseThrow();

            List<Item> itemsToExport = itemRepository.findItemsBySku(skuEntity).stream()
                    .filter(i -> i.getStatus() == Item.Status.queued)
                    .limit(dto.getQuantity())
                    .toList();

            itemsToExport.forEach(i -> i.setStatus(Item.Status.exported));
            itemRepository.saveAll(itemsToExport);

            ExportOrderDetail detail = ExportOrderDetail.builder()
                    .exportOrder(exportOrder)
                    .sku(skuEntity)
                    .quantity(itemsToExport.size())
                    .build();
            exportOrderDetailRepository.save(detail);

            responses.add(ExportItemResponse.builder()
                    .skuCode(skuEntity.getSkuCode())
                    .quantity(itemsToExport.size())
                    .exportCode(exportCode)
                    .exportDate(LocalDateTime.now())
                    .exportDateString(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd")))
                    .build());
        }

        // --- Tạo PickingRouteRequest ---
        PickingRouteRequest routeRequest = new PickingRouteRequest();
        routeRequest.setSkuList(responses.stream()
                .map(r -> new PickingRouteRequest.SKURequest(r.getSkuCode(), r.getQuantity()))
                .toList());

        List<PickingRouteResponse> pickingRoutes = pickingRouteService.getOptimalPickingRoute(routeRequest);

        // --- Lưu log vào bảng export_log ---
        ObjectMapper objectMapper = new ObjectMapper(); // Jackson
        String pickingRouteJson = objectMapper.writeValueAsString(pickingRoutes);

        for (ExportItemResponse response : responses) {
            ExportLog log = ExportLog.builder()
                    .exportCode(response.getExportCode())
                    .skuCode(response.getSkuCode())
                    .quantity(response.getQuantity())
                    .exportedBy(Long.valueOf(createdBy.getId()))
                    .exportDate(response.getExportDate())
                    .exportDateString(response.getExportDateString())
                    .destination(exportOrder.getDestination())
                    .status(exportOrder.getStatus().name())
                    .source(exportOrder.getSource().name())
                    .urgent(exportOrder.getUrgent())
                    .pickingRoute(pickingRouteJson)
                    .build();
            exportLogRepository.save(log);
        }

        return new ExportWithPickingRouteResponse(responses, pickingRoutes);
    }

    @Transactional
    public void updateNoteForExportCode(String exportCode, String note) {
        // Cập nhật export_log
        exportLogRepository.updateNoteByExportCode(exportCode, note);

        // Cập nhật export_order
        exportOrderRepository.updateNoteByExportCode(exportCode, note);
    }


    @Transactional
    public void moveItemsToQueue(List<ExportItemRequest.ExportQueueDTO> dtos) {
        for (ExportItemRequest.ExportQueueDTO dto : dtos) {
            SKU skuEntity = skuRepository.findBySkuCode(dto.getSku())
                    .orElseThrow(() -> new RuntimeException("SKU không tồn tại: " + dto.getSku()));

            List<Item> availableItems = itemRepository.findItemsBySku(skuEntity).stream()
                    .filter(i -> i.getStatus() == Item.Status.available)
                    .limit(dto.getQuantity())
                    .toList();

            for (Item item : availableItems) {
                item.setStatus(Item.Status.queued); // chuyển sang trạng thái chờ xuất
                itemRepository.save(item);
            }
        }
    }

    @Transactional
    public void moveItemsBackFromQueue(List<ExportItemRequest.ExportQueueDTO> dtos) {
        for (ExportItemRequest.ExportQueueDTO dto : dtos) {
            SKU skuEntity = skuRepository.findBySkuCode(dto.getSku())
                    .orElseThrow(() -> new RuntimeException("SKU không tồn tại: " + dto.getSku()));

            // Lấy các item đang queued theo SKU, giới hạn theo số lượng
            List<Item> queuedItems = itemRepository.findItemsBySku(skuEntity).stream()
                    .filter(i -> i.getStatus() == Item.Status.queued)
                    .limit(dto.getQuantity())
                    .toList();

            // Cập nhật trạng thái về available
            queuedItems.forEach(i -> i.setStatus(Item.Status.available));

            // Lưu tất cả cùng lúc
            itemRepository.saveAll(queuedItems);
        }
    }

    public int countSkuAvailable(SKU sku) {
        return (int) itemRepository.findItemsBySku(sku).stream()
                .filter(i -> i.getStatus() == Item.Status.available)
                .count();
    }

    public int countSkuQueued(SKU sku) {
        return (int) itemRepository.findItemsBySku(sku).stream()
                .filter(i -> i.getStatus() == Item.Status.queued)
                .count();
    }

    public List<SKUStatusResponse> getAllSkuStatus() {
        List<SKU> allSkus = skuRepository.findAll();
        return allSkus.stream().map(sku -> {
            int available = countSkuAvailable(sku);
            int queued = countSkuQueued(sku);
            return new SKUStatusResponse(sku.getSkuCode(), available, queued);
        }).toList();
    }

    public ExportInfoResponse getLatestExportInfo() throws JsonProcessingException {
        // Lấy tất cả bản ghi log của exportCode mới nhất
        List<ExportLog> logs = exportLogRepository.findLatestExportLogs();
        if (logs.isEmpty()) {
            return null;
        }

        ExportLog latestLog = logs.get(0);
        String pickingRouteJson = latestLog.getPickingRoute(); // JSON lộ trình

        // Deserialize JSON thành List<PickingRouteResponse>
        ObjectMapper objectMapper = new ObjectMapper();
        List<PickingRouteResponse> pickingRoutes = new ArrayList<>();
        if (pickingRouteJson != null && !pickingRouteJson.isEmpty()) {
            pickingRoutes = objectMapper.readValue(
                    pickingRouteJson,
                    new TypeReference<List<PickingRouteResponse>>() {}
            );
        }

        // Tạo DTO trả về
        ExportInfoResponse dto = new ExportInfoResponse();
        dto.setExportCode(latestLog.getExportCode());
        dto.setExportDate(latestLog.getExportDate());
        dto.setNote(latestLog.getNote());
        dto.setPickingRoutes(pickingRoutes); // Set danh sách đã deserialize

        return dto;
    }



}
