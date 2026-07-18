package com.staj.stock.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

@Data
@Entity
public class Stock {

    @Id
    private String code;

    private int quantity;

    @Column(name = "unit_sale_price")
    private double unitSalePrice;
}
