package com.example.warehousesystem.controller;

import com.example.warehousesystem.dto.request.UserLockRequest;
import com.example.warehousesystem.dto.request.UserCreateRequest;
import com.example.warehousesystem.dto.request.UserUpdateRequest;
import com.example.warehousesystem.dto.response.ApiResponse;
import com.example.warehousesystem.dto.response.UserLockResponse;
import com.example.warehousesystem.dto.response.UserResponse;
import com.example.warehousesystem.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final UserService userService;

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<UserResponse>> createUser(@RequestBody UserCreateRequest request) {
        UserResponse response = userService.createUser(request);
        return ResponseEntity.ok(
                ApiResponse.<UserResponse>builder()
                        .message("Tạo user thành công")
                        .data(response)
                        .build()
        );
    }

    @PutMapping("/update/{userId}")
    public ResponseEntity<ApiResponse<UserResponse>> updateUser(
            @PathVariable Integer userId,
            @RequestBody UserUpdateRequest request
    ) {
        UserResponse response = userService.updateUser(userId, request);
        return ResponseEntity.ok(
                ApiResponse.<UserResponse>builder()
                        .message("Cập nhật user thành công")
                        .data(response)
                        .build()
        );
    }

    @GetMapping("/getUsers")
    public ResponseEntity<ApiResponse<List<UserResponse>>> getAllUsers() {
        List<UserResponse> users = userService.getAllUsers();
        return ResponseEntity.ok(
                ApiResponse.<List<UserResponse>>builder()
                        .message("Lấy danh sách user thành công")
                        .data(users)
                        .build()
        );
    }

    @PutMapping("/{userId}/lock")
    public ResponseEntity<ApiResponse<UserLockResponse>> lockOrUnlockUser(
            @PathVariable Integer userId,
            @RequestBody UserLockRequest request
    ) {
        UserLockResponse response = userService.lockOrUnlockUser(userId, request);
        return ResponseEntity.ok(
                ApiResponse.<UserLockResponse>builder()
                        .message("Thao tác khóa/mở khóa user thành công")
                        .data(response)
                        .build()
        );
    }

}
