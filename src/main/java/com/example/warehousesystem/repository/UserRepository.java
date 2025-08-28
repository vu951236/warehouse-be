package com.example.warehousesystem.repository;

import com.example.warehousesystem.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {

    // Tìm theo username (dùng để đăng nhập)
    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    @Query("SELECT u FROM User u " +
            "WHERE (:userCode IS NULL OR u.userCode LIKE %:userCode%) " +
            "AND (:fullName IS NULL OR u.fullName LIKE %:fullName%) " +
            "AND (:role IS NULL OR u.role = :role)")
    List<User> searchUsers(
            @Param("userCode") String userCode,
            @Param("fullName") String fullName,
            @Param("role") User.Role role
    );



}
