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
@Table(name = "userlog")
public class UserLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "action", nullable = false, length = 255)
    private String action;

    @Column(name = "target_table", length = 100)
    private String targetTable;

    @Column(name = "target_id")
    private Integer targetId;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @Column(name = "timestamp")
    private LocalDate timestamp;

    @Column(name = "ip_address", length = 100)
    private String ipAddress;

    @Column(name = "note", columnDefinition = "TEXT")
    private String note;
}
