package com.staj.stock.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
public class StockEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String code;

    @Column(name = "quantity_added")
    private int quantityAdded;

    @Column(name = "total_price_paid")
    private double totalPricePaid;

    @Column(name = "unit_price")
    private double unitPrice;

    private String vendor;

    @Column(name = "created_at")
    private LocalDateTime createdAt;


}
