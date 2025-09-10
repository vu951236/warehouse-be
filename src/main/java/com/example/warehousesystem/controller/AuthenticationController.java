package com.example.warehousesystem.controller;

import com.example.warehousesystem.config.CookieProperties;
import com.example.warehousesystem.Annotation.SystemLog;
import com.example.warehousesystem.dto.request.ForgotPasswordRequest;
import com.example.warehousesystem.dto.response.ApiResponse;
import com.example.warehousesystem.dto.request.AuthenticationRequest;
import com.example.warehousesystem.dto.request.IntrospectRequest;
import com.example.warehousesystem.dto.response.AuthenticationResponse;
import com.example.warehousesystem.exception.AppException;
import com.example.warehousesystem.exception.ErrorCode;
import com.example.warehousesystem.service.AuthenticationService;
import com.nimbusds.jose.JOSEException;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;
    private final CookieProperties cookieProperties;

    @PostMapping
    @SystemLog(action = "Đăng nhập hệ thống", targetTable = "auth")
    public ResponseEntity<ApiResponse<AuthenticationResponse>> login(
            @RequestBody AuthenticationRequest request,
            HttpServletResponse response) {

        AuthenticationResponse authenticationResponse = authenticationService.login(request);

        ResponseCookie refreshCookie = buildCookie(
                authenticationResponse.getRefreshToken(),
                30 * 24 * 60 * 60 // 30 ngày
        );

        response.setHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());

        return ResponseEntity.ok(ApiResponse.<AuthenticationResponse>builder()
                .data(authenticationResponse)
                .message("Đăng nhập thành công")
                .build());
    }

    @PostMapping("/logout")
    @SystemLog(action = "Đăng xuất hệ thống", targetTable = "auth")
    public ResponseEntity<ApiResponse<Void>> logout(
            @RequestBody IntrospectRequest request,
            @CookieValue(name = "refresh_token", required = false) String refreshToken,
            HttpServletResponse response) throws ParseException, JOSEException {

        authenticationService.logout(request, refreshToken);

        ResponseCookie clearCookie = buildCookie("", 0);
        response.setHeader(HttpHeaders.SET_COOKIE, clearCookie.toString());

        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .message("Đăng xuất thành công")
                .build());
    }

    @PostMapping("/refresh-token")
    @SystemLog(action = "Làm mới access token", targetTable = "auth")
    public ResponseEntity<ApiResponse<AuthenticationResponse>> refreshToken(
            @CookieValue(name = "refresh_token", required = false) String refreshToken
    ) throws ParseException, JOSEException {

        if (refreshToken == null) {
            throw new AppException(ErrorCode.INVALID_REFRESH_TOKEN);
        }

        AuthenticationResponse newAccessToken = authenticationService.refreshAccessToken(refreshToken);

        ResponseCookie refreshCookie = buildCookie(
                newAccessToken.getRefreshToken(),
                30 * 24 * 60 * 60
        );

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
                .body(ApiResponse.<AuthenticationResponse>builder()
                        .data(newAccessToken)
                        .message("Cập nhật access token thành công")
                        .build());
    }

    @PostMapping("/forgot-password")
    @SystemLog(action = "Yêu cầu quên mật khẩu", targetTable = "auth")
    public ResponseEntity<ApiResponse<Void>> forgotPassword(@RequestBody ForgotPasswordRequest request) {
        authenticationService.forgotPassword(request);

        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .message("Nếu email tồn tại, mật khẩu mới đã được gửi.")
                .build());
    }

    private ResponseCookie buildCookie(String value, long maxAge) {
        return ResponseCookie.from("refresh_token", value)
                .httpOnly(true)
                .secure(cookieProperties.isSecure())
                .sameSite(cookieProperties.getSameSite())
                .path("/")
                .maxAge(maxAge)
                .build();
    }
}
