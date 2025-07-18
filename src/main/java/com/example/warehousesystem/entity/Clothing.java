package com.example.warehousesystem.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "clothing")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Clothing {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String size;
    private String color;
    private double price;

    // Constructor không có id
    public Clothing(String name, String size, String color, double price) {
        this.name = name;
        this.size = size;
        this.color = color;
        this.price = price;
    }
}
