package com.example.warehousesystem.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "user_code", nullable = false, unique = true, length = 50)
    private String userCode;

    @Column(name = "username", nullable = false, unique = true, length = 100)
    private String username;

    @Column(name = "email", nullable = false, unique = true, length = 255)
    private String email;

    @Column(name = "full_name", length = 255)
    private String fullName;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 10)
    private Role role;

    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @Column(name = "created_at")
    private LocalDate createdAt;

    @Column(name = "is_active")
    private Boolean isActive;

    public enum Role {
        admin,
        staff
    }
}
