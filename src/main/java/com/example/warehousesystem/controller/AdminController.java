package com.example.warehousesystem.controller;

import com.example.warehousesystem.Annotation.SystemLog;
import com.example.warehousesystem.dto.request.SearchUserRequest;
import com.example.warehousesystem.dto.request.UserLockRequest;
import com.example.warehousesystem.dto.request.UserCreateRequest;
import com.example.warehousesystem.dto.request.UserUpdateRequest;
import com.example.warehousesystem.dto.response.ApiResponse;
import com.example.warehousesystem.dto.response.UserLockResponse;
import com.example.warehousesystem.dto.response.UserResponse;
import com.example.warehousesystem.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final UserService userService;

    @PostMapping("/create")
    @SystemLog(action = "Tạo người dùng", targetTable = "user")
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
    @SystemLog(action = "Cập nhật thông tin người dùng", targetTable = "user")
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
    @SystemLog(action = "Lấy danh sách người dùng", targetTable = "user")
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
    @SystemLog(action = "Khóa/Mở khóa người dùng", targetTable = "user")
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

    @PutMapping("/{userId}/reset-password")
    @PreAuthorize("hasRole('admin')")
    @SystemLog(action = "Đặt lại mật khẩu người dùng", targetTable = "user")
    public ResponseEntity<ApiResponse<Void>> resetPasswordToDefault(@PathVariable Integer userId) {
        userService.resetPasswordToDefault(userId);
        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .message("Đặt lại mật khẩu thành công (mặc định: Nhom333@)")
                        .build()
        );
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasRole('admin')")
    @SystemLog(action = "Xem thông tin chi tiết người dùng", targetTable = "user")
    public ResponseEntity<ApiResponse<UserResponse>> getUserById(@PathVariable Integer userId) {
        UserResponse response = userService.getUserById(userId);
        return ResponseEntity.ok(
                ApiResponse.<UserResponse>builder()
                        .message("Lấy thông tin user thành công")
                        .data(response)
                        .build()
        );
    }

    @PostMapping("/search")
    @SystemLog(action = "Tìm kiếm người dùng", targetTable = "user")
    public ResponseEntity<ApiResponse<List<UserResponse>>> searchUsers(@RequestBody SearchUserRequest request) {
        List<UserResponse> users = userService.searchUsers(request);
        return ResponseEntity.ok(
                ApiResponse.<List<UserResponse>>builder()
                        .code(200)
                        .message("Tìm kiếm người dùng thành công")
                        .data(users)
                        .build()
        );
    }

}
