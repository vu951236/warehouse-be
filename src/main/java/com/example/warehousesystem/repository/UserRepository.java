package com.example.warehousesystem.repository;

import com.example.warehousesystem.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
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

    // Tìm tất cả người dùng theo vai trò (admin hoặc staff)
    List<User> findByRole(User.Role role);

    // Tìm kiếm theo tên đầy đủ (fullName)
    List<User> findByFullNameContainingIgnoreCase(String keyword);

    // Tìm tất cả người dùng đang hoạt động
    List<User> findByIsActiveTrue();

    // Tìm tất cả người dùng đã bị khóa
    List<User> findByIsActiveFalse();

}
