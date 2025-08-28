package com.example.warehousesystem.dto.request;

import com.example.warehousesystem.entity.User;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SearchUserRequest {
    private String userCode;   // mã nhân viên
    private String fullName;   // tên nhân viên
    private User.Role role;    // vai trò (admin, staff, ...)
}
