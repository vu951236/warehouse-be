package com.example.warehousesystem.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ResetPasswordRequest {
    private String email;
    private String code;         // Mã xác minh được gửi qua email
    private String newPassword;  // Mật khẩu mới
}
