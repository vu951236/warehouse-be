package com.example.warehousesystem.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
//Tạo tài khoản (admin)
public class UserCreateRequest {
    private String username;
    private String email;
    private String password;
    private String fullName;
    private String role;
    private Boolean isActive;  // trạng thái tài khoản
}
