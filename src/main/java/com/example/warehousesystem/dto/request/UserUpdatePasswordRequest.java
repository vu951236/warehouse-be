package com.example.warehousesystem.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
//Đổi mật khẩu(đã đăng nhập)
public class UserUpdatePasswordRequest {
    private String oldPassword;
    private String newPassword;
}
