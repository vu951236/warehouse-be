package com.example.warehousesystem.mapper;

import com.example.warehousesystem.dto.request.UserCreateRequest;
import com.example.warehousesystem.dto.request.UserUpdateRequest;
import com.example.warehousesystem.dto.response.ProfileResponse;
import com.example.warehousesystem.dto.response.UserLockResponse;
import com.example.warehousesystem.dto.response.UserResponse;
import com.example.warehousesystem.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UserMapper {

    User toUser(UserCreateRequest request);

    @Mapping(source = "id", target = "userId")
    @Mapping(source = "userCode", target = "userCode")
    @Mapping(source = "createdAt", target = "createdAt")
    UserResponse toUserResponse(User user);


    void updateUser(@MappingTarget User user, UserUpdateRequest request);

    @Mapping(source = "email", target = "email")
    @Mapping(source = "fullName", target = "fullName")
    ProfileResponse toProfileResponse(User user);

    @Mapping(source = "id", target = "userId")
    @Mapping(source = "username", target = "username")
    @Mapping(source = "isActive", target = "isActive")
    UserLockResponse toUserLockResponse(User user);
}
