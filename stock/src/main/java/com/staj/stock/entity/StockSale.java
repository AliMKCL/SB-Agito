package com.staj.stock.entity;

import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@Getter
@Setter
public class StockSale {

    public StockSale(String code, double payment, int amount, String buyerName, LocalDateTime soldAt){
        this.code = code;
        this.payment = payment;
        this.amount = amount;
        this.buyerName = buyerName;
        this.soldAt = soldAt;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String code;

    private double payment;

    private int amount;

    @Column(name = "buyer_name")
    @Nullable
    private String buyerName;

    @Column(name = "sold_at")
    private LocalDateTime soldAt;
}
