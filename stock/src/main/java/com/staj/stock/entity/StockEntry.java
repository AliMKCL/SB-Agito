package com.staj.stock.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;

import java.util.Date;

@Entity
@Data
public class StockEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String code;

    @Column(name = "quantity_added")
    private int quantityAdded;

    @Column(name = "total_price")
    private double totalPrice;

    @Column(name = "unit_price")
    private double unitPrice;

    private String vendor;

    @Column(name = "created_at")
    private Date createdAt;


}
