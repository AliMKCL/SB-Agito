package com.agito.staj.dto;

import lombok.Data;

@Data
public class ProductDto {

    private String code;

    private String name;

    private String category;

    private double price;

    private int stock;
}
