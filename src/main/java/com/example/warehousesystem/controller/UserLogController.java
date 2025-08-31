package com.example.warehousesystem.controller;

import com.example.warehousesystem.Annotation.SystemLog;
import com.example.warehousesystem.dto.request.UserLogAllRequest;
import com.example.warehousesystem.dto.request.UserLogRequest;
import com.example.warehousesystem.dto.response.UserLogResponse;
import com.example.warehousesystem.service.UserLogService;
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
    @PostMapping("/search")
    @SystemLog(action = "Tìm kiếm nhật ký người dùng", targetTable = "user_log")
    public ResponseEntity<List<UserLogResponse>> getUserLogs(@RequestBody UserLogRequest request) {
        List<UserLogResponse> response = userLogService.getUserLogs(request);
        return ResponseEntity.ok(response);
    }

    /**
     * Lấy toàn bộ log theo warehouseId
     */
    @PostMapping("/all")
    @SystemLog(action = "Xem toàn bộ nhật ký người dùng theo kho", targetTable = "user_log")
    public ResponseEntity<List<UserLogResponse>> getAllUserLogs(@RequestBody UserLogAllRequest request) {
        List<UserLogResponse> response = userLogService.getAllUserLogs(request.getWarehouseId());
        return ResponseEntity.ok(response);
    }
}
