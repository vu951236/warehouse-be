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
@Table(name = "reorganizehistory")
public class ReorganizeHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "executed_by", nullable = false)
    private User executedBy;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @Column(name = "execution_time")
    private LocalDate executionTime;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
}
