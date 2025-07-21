package com.example.warehousesystem.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "reorganizehistory")
public class ReorganizeHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "executed_by", nullable = false)
    private User executedBy;

    @Column(name = "execution_time")
    private LocalDateTime executionTime;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
}
