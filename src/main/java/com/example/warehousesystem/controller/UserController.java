package com.example.warehousesystem.controller;

import com.example.warehousesystem.Annotation.SystemLog;
import com.example.warehousesystem.dto.request.*;
import com.example.warehousesystem.dto.response.ApiResponse;
import com.example.warehousesystem.dto.response.ProfileResponse;
import com.example.warehousesystem.dto.response.UserResponse;
import com.example.warehousesystem.service.UserService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {

    final UserService userService;

    /**
     * Cập nhật mật khẩu người dùng
     */
    @PutMapping("/updatePass")
    @SystemLog(action = "Cập nhật mật khẩu", targetTable = "user")
    ApiResponse<UserResponse> UpdatePassword(@RequestBody UserUpdatePasswordRequest request) {
        return ApiResponse.<UserResponse>builder()
                .data(userService.updatePassword(request))
                .build();
    }

    /**
     * Lấy thông tin người dùng hiện tại
     */
    @GetMapping("/getInfo")
    @SystemLog(action = "Xem thông tin cá nhân", targetTable = "user")
    ApiResponse<UserResponse> GetMyInfo() {
        return ApiResponse.<UserResponse>builder()
                .data(userService.getMyInfo())
                .build();
    }

    /**
     * Quên mật khẩu - gửi mã xác thực
     */
    @PostMapping("/forgot-password")
    @SystemLog(action = "Quên mật khẩu - gửi mã xác thực", targetTable = "user")
    ApiResponse<Void> forgotPassword(@RequestBody ForgotPasswordRequest request) throws IOException, MessagingException {
        userService.generateVerificationCode(request);
        return ApiResponse.<Void>builder().build();
    }

    /**
     * Xác thực mã quên mật khẩu
     */
    @PostMapping("/verify-code")
    @SystemLog(action = "Xác thực mã quên mật khẩu", targetTable = "user")
    ApiResponse<Boolean> verifyCode(@RequestBody VerifyCodeRequest request) {
        var verified = userService.verifyCode(request.getEmail(),request.getCode());
        return ApiResponse.<Boolean>builder()
                .data(verified)
                .build();
    }

    /**
     * Đặt lại mật khẩu mới sau khi xác thực
     */
    @PostMapping("/reset-password")
    @SystemLog(action = "Đặt lại mật khẩu", targetTable = "user")
    ApiResponse<Boolean> resetPassword(@RequestBody ResetPasswordRequest request) {
        var verified = userService.verifyCode(request.getEmail(),request.getCode());
        if (verified) {
            userService.resetPassword(request.getEmail(), request.getNewPassword());
            userService.clearCode(request.getEmail());
        }
        return ApiResponse.<Boolean>builder()
                .data(verified)
                .build();
    }

    /**
     * Cập nhật thông tin hồ sơ người dùng
     */
    @PutMapping("/update-profile")
    @SystemLog(action = "Cập nhật hồ sơ người dùng", targetTable = "user")
    public ApiResponse<ProfileResponse> updateProfile(
            @RequestBody ProfileUpdateRequest request
    ) {
        return ApiResponse.<ProfileResponse>builder()
                .data(userService.updateProfile(request))
                .build();
    }


}
