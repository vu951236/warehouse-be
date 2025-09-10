package com.example.warehousesystem.controller;

import com.example.warehousesystem.Annotation.SystemLog;
import com.example.warehousesystem.dto.request.UserLogAllRequest;
import com.example.warehousesystem.dto.request.UserLogRequest;
import com.example.warehousesystem.dto.response.ApiResponse;
import com.example.warehousesystem.dto.response.UserLogResponse;
import com.example.warehousesystem.service.UserLogService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user-logs")
@RequiredArgsConstructor
public class UserLogController {

    private final UserLogService userLogService;

    /**
     * Tìm kiếm log theo ngày + warehouseId
     */
    @SystemLog(action = "Tìm kiếm nhật ký người dùng", targetTable = "user_log")
    @PostMapping("/search")
    public ResponseEntity<ApiResponse<List<UserLogResponse>>> getUserLogs(
            @Valid @RequestBody UserLogRequest request) {

        List<UserLogResponse> data = userLogService.getUserLogs(request);

        return ResponseEntity.ok(
                ApiResponse.<List<UserLogResponse>>builder()
                        .message("Tìm kiếm nhật ký người dùng thành công")
                        .data(data)
                        .build()
        );
    }

    /**
     * Lấy toàn bộ log theo warehouseId
     */
    @SystemLog(action = "Xem toàn bộ nhật ký người dùng theo kho", targetTable = "user_log")
    @PostMapping("/all")
    public ResponseEntity<ApiResponse<List<UserLogResponse>>> getAllUserLogs(
            @Valid @RequestBody UserLogAllRequest request) {

        List<UserLogResponse> data = userLogService.getAllUserLogs(request.getWarehouseId());

        return ResponseEntity.ok(
                ApiResponse.<List<UserLogResponse>>builder()
                        .message("Lấy toàn bộ nhật ký người dùng thành công")
                        .data(data)
                        .build()
        );
    }
}
