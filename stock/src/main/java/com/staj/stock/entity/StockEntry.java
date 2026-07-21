package com.staj.stock.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@Getter
@Setter
public class StockEntry {

    public StockEntry(String code, int quantity_added, double total_price_paid, double unit_price, String vendor, LocalDateTime createdAt){
        this.code = code;
        this.quantityAdded = quantity_added;
        this.totalPricePaid = total_price_paid;
        this.unitPrice = unit_price;
        this.vendor = vendor;
        this.createdAt = createdAt;
    }

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
