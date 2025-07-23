package com.example.warehousesystem.repository;

import com.example.warehousesystem.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {

    // Tìm theo username (dùng để đăng nhập)
    Optional<User> findByUsername(String username);

    // Tìm theo email (dùng cho quên mật khẩu)
    Optional<User> findByEmail(String email);

    // Có thể thêm nếu dùng xác thực bằng username hoặc email
    Optional<User> findByUsernameOrEmail(String username, String email);

    // Kiểm tra tồn tại (kiểm tra khi đăng ký)
    boolean existsByUsername(String username);

    boolean existsByEmail(String email);
}
