package com.example.warehousesystem.dto.request;

import lombok.Data;

@Data
//Đổi mật khẩu
public class ChangePasswordRequest {
    private String oldPassword;
    private String newPassword;
}
