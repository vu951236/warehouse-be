package com.example.warehousesystem.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "exportorderdetail")
public class ExportOrderDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "export_order_id", nullable = false)
    private ExportOrder exportOrder;

    @ManyToOne
    @JoinColumn(name = "sku_id", nullable = false)
    private SKU sku;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Column(name = "allocated_quantity", nullable = false)
    private Integer allocatedQuantity;
}
