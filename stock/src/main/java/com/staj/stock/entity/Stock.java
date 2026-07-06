package com.staj.stock.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

@Data
@Entity
public class Stock {

    @Id
    private String code;

    private int quantity;
}
