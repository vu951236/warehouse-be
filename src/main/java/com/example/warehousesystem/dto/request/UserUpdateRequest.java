package com.example.warehousesystem.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
//Cập nhập thông tin tài khoản
public class UserUpdateRequest {
    private String email;
    private String username;
    private String fullName;
    private String role;
}
