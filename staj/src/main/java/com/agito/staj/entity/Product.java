package com.agito.staj.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class Product {

    @Id
    @Column(name="product_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String productId;

    // kod ekle (güncellemede find ederken vs kullanılacak)

    private String name;

    private String category;

    private double price;

    private int stock;

    public Product(String name, String category, double price, int stock){
        this.name = name;
        this.category = category;
        this.price = price;
        this.stock = stock;
    }
}
